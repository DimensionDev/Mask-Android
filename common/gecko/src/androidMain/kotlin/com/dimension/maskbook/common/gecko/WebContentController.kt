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
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.state.action.BrowserAction
import mozilla.components.browser.state.engine.EngineMiddleware
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.state.state.BrowserState
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.DefaultSettings
import mozilla.components.concept.engine.EngineSession
import mozilla.components.concept.engine.request.RequestInterceptor
import mozilla.components.concept.engine.webextension.MessageHandler
import mozilla.components.concept.engine.webextension.Port
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.tabs.TabsUseCases
import mozilla.components.lib.state.Store
import org.json.JSONObject
import org.mozilla.geckoview.GeckoRuntime
import java.io.Closeable

private const val PortName = "browser"
private const val TAG = "WebContentController"

private class MessageHolder : MessageHandler {
    private val _message = MutableSharedFlow<JSONObject>(extraBufferCapacity = Int.MAX_VALUE)
    val message = _message.asSharedFlow()
    private val _port = MutableStateFlow<Port?>(null)
    val connected = _port.map { it != null }
    override fun onPortConnected(port: Port) {
        _port.tryEmit(port)
    }

    override fun onPortDisconnected(port: Port) {
        _port.tryEmit(null)
    }

    override fun onPortMessage(message: Any, port: Port) {
        when (message) {
            is JSONObject -> message
            is String -> JSONObject(message)
            else -> null
        }?.let {
            _message.tryEmit(it)
        }
    }

    fun sendMessage(message: JSONObject) {
        _port.value?.postMessage(JSONObject(mapOf("result" to message.toString())))
    }
}

class WebContentController(
    context: Context,
    private val scope: CoroutineScope,
    var onNavigate: (String) -> Boolean = { true },
) : Closeable {
    private val _browserState = MutableStateFlow<BrowserState?>(null)
    private lateinit var _observer: Store.Subscription<BrowserState, BrowserAction>
    private val _activeTabId = _browserState.mapNotNull { it?.selectedTab?.id }
    private val _contentMessageHolders = MutableStateFlow(mapOf<String, MessageHolder>())
    private val _backgroundMessageHolder = MessageHolder()

    @OptIn(ExperimentalCoroutinesApi::class)
    val contentMessage: Flow<JSONObject> =
        combine(_contentMessageHolders, _activeTabId) { holders, activeTabId ->
            holders[activeTabId]?.message
        }.mapNotNull { it }.flatMapLatest { it }
    val backgroundMessage = _backgroundMessageHolder.message.map {
        Log.i(TAG, "onBackgroundMessage: $it")
        it
    }
    val isExtensionConnected = _backgroundMessageHolder.connected
    private val runtime by lazy {
        GeckoRuntime.create(context)
    }

    private val _interceptorUri = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val interceptorUri = _interceptorUri.asSharedFlow()

    private val interceptor by lazy {
        object : RequestInterceptor {
            override fun onLoadRequest(
                engineSession: EngineSession,
                uri: String,
                lastUri: String?,
                hasUserGesture: Boolean,
                isSameDomain: Boolean,
                isRedirect: Boolean,
                isDirectNavigation: Boolean,
                isSubframeRequest: Boolean
            ): RequestInterceptor.InterceptionResponse? {
                if (isSubframeRequest) {
                    return null
                }
                return if (!onNavigate(uri)) {
                    _interceptorUri.tryEmit(uri)
                    RequestInterceptor.InterceptionResponse.AppIntent(
                        Intent(Intent.ACTION_VIEW, Uri.parse(uri)),
                        uri
                    )
                } else {
                    null
                }
            }
        }
    }
    internal val engine by lazy {
        GeckoEngine(
            context,
            runtime = runtime,
            defaultSettings = DefaultSettings(
                remoteDebuggingEnabled = BuildConfig.DEBUG,
                requestInterceptor = interceptor
            )
        )
    }
    internal val store by lazy {
        BrowserStore(
            middleware = EngineMiddleware.create(engine)
        ).apply {
            _observer = observeManually {
                _browserState.value = it
            }.apply {
                resume()
            }
        }
    }
    private val tabsUseCases by lazy {
        TabsUseCases(store)
    }
    private val sessionUseCases by lazy {
        SessionUseCases(store)
    }

    fun installExtensions(
        id: String,
        url: String,
    ) {
        engine.installWebExtension(
            id,
            url,
            onSuccess = { extension ->
                extension.registerBackgroundMessageHandler(PortName, _backgroundMessageHolder)
                _browserState.mapNotNull { it?.tabs }.onEach { list ->
                    list.forEach { tab ->
                        tab.engineState.engineSession?.let { session ->
                            if (!extension.hasContentMessageHandler(session, PortName)) {
                                val holder = MessageHolder()
                                _contentMessageHolders.value += tab.id to holder
                                extension.registerContentMessageHandler(
                                    session,
                                    PortName,
                                    holder,
                                )
                            }
                        }
                    }
                }.launchIn(scope)
                _browserState.mapNotNull { it?.closedTabs }.onEach { list ->
                    list.forEach { tab ->
                        _contentMessageHolders.value -= tab.id
                    }
                }.launchIn(scope)
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

    fun createPromptFeature(fragmentActivity: FragmentActivity): PromptFeatureDelegate {
        return PromptFeatureDelegate(
            fragmentActivity = fragmentActivity,
            store = store,
        )
    }

    override fun close() {
        _observer.unsubscribe()
        runtime.shutdown()
    }

    fun sendContentMessage(message: JSONObject) {
        Log.i(TAG, "sendContentMessage: $message")
        scope.launch {
            _contentMessageHolders.firstOrNull()?.let { holders ->
                _activeTabId.firstOrNull()?.let { tabId ->
                    holders[tabId]?.sendMessage(message)
                }
            }
        }
    }

    fun sendBackgroundMessage(message: JSONObject) {
        Log.i(TAG, "sendBackgroundMessage: $message")
        _backgroundMessageHolder.sendMessage(message)
    }
}
