/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.common.gecko

import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.state.engine.EngineMiddleware
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.state.state.BrowserState
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.DefaultSettings
import mozilla.components.concept.engine.webextension.MessageHandler
import mozilla.components.concept.engine.webextension.Port
import mozilla.components.feature.prompts.PromptFeature
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.tabs.TabsUseCases
import mozilla.components.lib.state.ext.observe
import org.json.JSONObject
import org.mozilla.geckoview.GeckoRuntime
import java.io.Closeable

private const val BackgroundPortName = "browser"
private const val PERMISSION_CODE = 34298

class WebContentController(
    fragmentActivity: FragmentActivity
) : Closeable {
    private val _browserState = MutableStateFlow<BrowserState?>(null)
    private var _port: Port? = null
    private val runtime by lazy {
        GeckoRuntime.create(fragmentActivity)
    }
    private val backPressedCallback by lazy {
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                val selectedTab = _browserState.value?.selectedTab
                if (selectedTab != null) {
                    if (selectedTab.content.canGoBack) {
                        sessionUseCases.goBack()
                    } else {
                        tabsUseCases.removeTab(selectedTab.id)
                    }
                }
            }
        }
    }
    private val promptFeature by lazy {
        PromptFeature(
            activity = fragmentActivity,
            fragmentManager = fragmentActivity.supportFragmentManager,
            store = store,
            onNeedToRequestPermissions = { permissions ->
                this.permissionsRequest.launch(permissions)
            }
        )
    }
    // Make sure register the permission request when the activity is created
    private val permissionsRequest: ActivityResultLauncher<Array<String>> =
        fragmentActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            promptFeature.onPermissionsResult(
                it.keys.toTypedArray(),
                it.values.map { if (it) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED }
                    .toIntArray()
            )
        }
    internal val engine by lazy {
        GeckoEngine(
            fragmentActivity,
            runtime = runtime,
            defaultSettings = DefaultSettings(
                remoteDebuggingEnabled = BuildConfig.DEBUG,
            )
        )
    }
    internal val store by lazy {
        BrowserStore(
            middleware = EngineMiddleware.create(engine)
        ).apply {
            observe(fragmentActivity) {
                _browserState.value = it
            }
        }
    }
    private val tabsUseCases by lazy {
        TabsUseCases(store)
    }
    private val sessionUseCases by lazy {
        SessionUseCases(store)
    }
    private val _message = MutableStateFlow<JSONObject?>(null)
    val message = _message.asSharedFlow()
    private val messageHandler = object : MessageHandler {
        override fun onPortConnected(port: Port) {
            _port = port
        }

        override fun onPortDisconnected(port: Port) {
            _port = null
        }

        override fun onPortMessage(message: Any, port: Port) {
            if (message is JSONObject) {
                _message.value = message
            }
        }
    }

    init {
        require(!fragmentActivity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            "WebContentController must be created before the activity is started"
        }
        fragmentActivity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                promptFeature.start()
            }

            override fun onStop(owner: LifecycleOwner) {
                promptFeature.stop()
            }
        })
        fragmentActivity.onBackPressedDispatcher.addCallback(fragmentActivity, backPressedCallback)
        fragmentActivity.lifecycleScope.launch {
            _browserState.mapNotNull { it }.collect {
                backPressedCallback.isEnabled = it.selectedTab != null
            }
        }
    }

    fun installExtensions(
        id: String,
        url: String,
    ) {
        engine.installWebExtension(
            id,
            url,
            onSuccess = {
                it.registerBackgroundMessageHandler(BackgroundPortName, messageHandler)
            }
        )
    }

    fun newTab(
        url: String = "about:blank",
        selectTab: Boolean = true
    ) = tabsUseCases.addTab(
        url = url,
        selectTab = selectTab
    )

    fun closeTab(id: String) = tabsUseCases.removeTab(id)
    fun switchTab(id: String) = tabsUseCases.selectTab(id)
    fun loadUrl(url: String) = sessionUseCases.loadUrl(url)
    fun refresh() = sessionUseCases.reload()
    fun goBack() = sessionUseCases.goBack()
    fun goForward() = sessionUseCases.goForward()
    val canGoBack get() = _browserState.mapNotNull { it?.selectedTab?.content?.canGoBack }
    val canGoForward get() = _browserState.mapNotNull { it?.selectedTab?.content?.canGoForward }
    val url get() = _browserState.mapNotNull { it?.selectedTab?.content?.url }
    val title get() = _browserState.mapNotNull { it?.selectedTab?.content?.title }
    val tabCount get() = _browserState.mapNotNull { it?.tabs?.size }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        promptFeature.onActivityResult(requestCode, data, resultCode)
    }

    fun onBiometricResult(isAuthenticated: Boolean) {
        promptFeature.onBiometricResult(isAuthenticated)
    }

    override fun close() {
        runtime.shutdown()
    }

    fun sendMessage(message: JSONObject) {
        _port?.postMessage(message)
    }
}
