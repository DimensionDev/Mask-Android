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

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapNotNull
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.state.action.BrowserAction
import mozilla.components.browser.state.engine.EngineMiddleware
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.state.state.BrowserState
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.DefaultSettings
import mozilla.components.concept.engine.webextension.MessageHandler
import mozilla.components.concept.engine.webextension.Port
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.tabs.TabsUseCases
import mozilla.components.lib.state.Store
import org.json.JSONObject
import org.mozilla.geckoview.GeckoRuntime
import java.io.Closeable

private const val BackgroundPortName = "background"

class WebContentController(
    context: Context
) : Closeable {
    private lateinit var _observer: Store.Subscription<BrowserState, BrowserAction>
    private val _browserState = MutableStateFlow<BrowserState?>(null)
    private var _port: Port? = null
    private val runtime by lazy {
        GeckoRuntime.create(context)
    }
    internal val engine by lazy {
        GeckoEngine(
            context,
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
            _observer = observeManually {
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

    override fun close() {
        _observer.unsubscribe()
        runtime.shutdown()
    }

    fun sendMessage(message: JSONObject) {
        _port?.postMessage(message)
    }
}
