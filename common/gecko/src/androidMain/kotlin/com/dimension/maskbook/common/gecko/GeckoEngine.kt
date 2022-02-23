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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import org.mozilla.geckoview.AllowOrDeny
import org.mozilla.geckoview.BuildConfig
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.WebExtension
import org.mozilla.geckoview.WebExtension.SessionTabDelegate
import org.mozilla.geckoview.WebExtensionController
import org.mozilla.geckoview.WebResponse
import java.io.Closeable

class TabSession internal constructor(
    settings: GeckoSessionSettings? = null,
    extensionManager: ExtensionManager,
    tabDelegate: SessionTabDelegate,
    private val onClose: (TabSession) -> Unit,
    private val onNewTab: () -> GeckoSession?,
    private val onLoadRequest: (String) -> Boolean,
) : GeckoSession(settings),
    GeckoSession.ContentDelegate,
    GeckoSession.ProgressDelegate,
    GeckoSession.NavigationDelegate {
    val fullscreen = MutableStateFlow(false)
    val canGoBack = MutableStateFlow(false)
    val canGoForward = MutableStateFlow(false)
    val url = MutableStateFlow("")

    init {
        contentDelegate = this
        progressDelegate = this
        navigationDelegate = this
        // promptDelegate = this // TODO
        // permissionDelegate = this // TODO
        // mediaDelegate = this // TODO
        // selectionActionDelegate = this // TODO
        extensionManager.extension?.let { extension ->
            webExtensionController.setActionDelegate(extension, extensionManager)
            webExtensionController.setTabDelegate(extension, tabDelegate)
        }
    }

    override fun loadUri(uri: String) {
        super.loadUri(uri)
        url.value = uri
    }

    override fun onLocationChange(session: GeckoSession, url: String?) {
        super.onLocationChange(session, url)
        this.url.value = url ?: ""
    }

    override fun onFullScreen(session: GeckoSession, fullScreen: Boolean) {
        fullscreen.value = fullScreen
    }

    override fun onCloseRequest(session: GeckoSession) {
        onClose.invoke(this)
    }

    override fun onExternalResponse(session: GeckoSession, response: WebResponse) {
        // TODO
    }

    override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
        this.canGoBack.value = canGoBack
    }

    override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
        this.canGoForward.value = canGoForward
    }

    override fun onLoadRequest(
        session: GeckoSession,
        request: NavigationDelegate.LoadRequest
    ): GeckoResult<AllowOrDeny> {
        return if (onLoadRequest.invoke(request.uri)) {
            GeckoResult.fromValue(AllowOrDeny.ALLOW)
        } else {
            GeckoResult.fromValue(AllowOrDeny.DENY)
        }
    }

    override fun onNewSession(session: GeckoSession, uri: String): GeckoResult<GeckoSession> {
        return GeckoResult.fromValue(onNewTab.invoke())
    }
}

internal class TabManager : Closeable {
    private val tabs = MutableStateFlow(listOf<TabSession>())
    private val currentTabIndex = MutableStateFlow(0)
    val currentTab = combine(currentTabIndex, tabs) { index, tabs ->
        tabs.getOrNull(index)
    }
    val tabCount = tabs.map { it.size }

    operator fun get(index: Int): TabSession? {
        return tabs.value.elementAtOrNull(index)
    }

    private fun getCurrentTab(): TabSession? {
        return get(currentTabIndex.value)
    }

    fun setCurrentTab(tabSession: TabSession) {
        val index = tabs.value.indexOf(tabSession)
        if (index != -1) {
            currentTabIndex.value = index
        } else {
            tabs.value += tabSession
            currentTabIndex.value = tabs.value.size - 1
        }
    }

    fun createTab(
        extensionManager: ExtensionManager,
        tabDelegate: SessionTabDelegate,
        cookieStoreId: String? = null,
    ): TabSession {
        val settingsBuilder = GeckoSessionSettings.Builder()
        if (cookieStoreId != null) {
            settingsBuilder.contextId(cookieStoreId)
        }
        val tab = TabSession(
            settingsBuilder.build(),
            extensionManager,
            tabDelegate,
            onClose = {
                closeTab(it)
            },
            onNewTab = {
                createTab(extensionManager, tabDelegate, null)
            },
            onLoadRequest = {
                true
            },
        )
        tabs.value += tab
        return tab
    }

    fun closeTab(tabSession: TabSession) {
        if (!tabs.value.contains(tabSession)) {
            return
        }
        if (tabSession == tabs.value[currentTabIndex.value] && currentTabIndex.value == tabs.value.lastIndex) {
            currentTabIndex.value--
        }
        tabSession.close()
        tabs.value -= tabSession
    }

    fun setWebExtensionDelegates(
        extension: WebExtension,
        actionDelegate: WebExtension.ActionDelegate?,
        tabDelegate: WebExtension.SessionTabDelegate?
    ) {
        for (tab in tabs.value) {
            val sessionController = tab.webExtensionController
            sessionController.setActionDelegate(extension, actionDelegate)
            sessionController.setTabDelegate(extension, tabDelegate)
        }
    }

    override fun close() {
        for (tab in tabs.value) {
            tab.close()
        }
        tabs.value = listOf()
    }

    fun goBack(): Boolean {
        val currentTab = getCurrentTab()
        return if (currentTab != null) {
            if (currentTab.fullscreen.value) {
                currentTab.exitFullScreen()
                true
            } else if (currentTab.canGoBack.value) {
                currentTab.goBack()
                true
            } else if (tabs.value.size > 1) {
                closeTab(currentTab)
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    fun loadUrl(url: String) {
        getCurrentTab()?.loadUri(url)
    }
}

internal class ExtensionManager(
    private val messageFlow: MutableStateFlow<JSONObject?>,
) : WebExtension.ActionDelegate,
    WebExtension.MessageDelegate,
    WebExtension.PortDelegate {
    var extension: WebExtension? = null
        private set
    var port: WebExtension.Port? = null
        private set

    fun registerExtension(extension: WebExtension) {
        extension.setActionDelegate(this)
        extension.setMessageDelegate(this, "browser")
        this.extension = extension
    }

    override fun onConnect(port: WebExtension.Port) {
        this.port = port
        port.setDelegate(this)
    }

    override fun onDisconnect(port: WebExtension.Port) {
        if (this.port == port) {
            this.port = null
        }
    }

    override fun onPortMessage(message: Any, port: WebExtension.Port) {
        super.onPortMessage(message, port)
        if (message is JSONObject) {
            messageFlow.value = message
        }
    }

    fun sendMessage(message: JSONObject) {
        port?.postMessage(message)
    }
}

internal class GeckoEngine(
    context: Context,
) : WebExtensionController.DebuggerDelegate,
    WebExtension.TabDelegate,
    WebExtension.SessionTabDelegate,
    Closeable {
    val runtime: GeckoRuntime
    private val extensionManager: ExtensionManager
    private val tabManager = TabManager()
    private val _messageFlow = MutableStateFlow<JSONObject?>(null)
    val messageFlow = _messageFlow.asSharedFlow()
    val currentTab by lazy {
        tabManager.currentTab
    }
    val tabCount by lazy {
        tabManager.tabCount
    }
    init {
        val builder = GeckoRuntimeSettings.Builder().apply {
            remoteDebuggingEnabled(BuildConfig.DEBUG)
            consoleOutput(BuildConfig.DEBUG)
            aboutConfigEnabled(BuildConfig.DEBUG)
            if (BuildConfig.DEBUG) {
                // In debug builds, we want to load JavaScript resources fresh with
                // each build.
                arguments(arrayOf("-purgecaches"))
            }
        }
        runtime = GeckoRuntime.create(context, builder.build())
        extensionManager = ExtensionManager(_messageFlow)
        runtime.webExtensionController.setDebuggerDelegate(this)
    }

    override fun onExtensionListUpdated() {
        runtime.webExtensionController.list().accept { list ->
            list?.forEach {
                it.tabDelegate = this
                extensionManager.registerExtension(it)
                tabManager.setWebExtensionDelegates(
                    it,
                    extensionManager,
                    this
                )
            }
        }
    }

    fun loadUrl(url: String) {
        tabManager.loadUrl(url)
    }

    fun ensureBuiltInExtension(id: String, uri: String) {
        TODO("Not yet implemented")
    }

    fun goBack(): Boolean {
        return tabManager.goBack()
    }

    fun sendMessage(message: JSONObject) {
        extensionManager.sendMessage(message)
    }

    override fun onNewTab(
        source: WebExtension,
        createDetails: WebExtension.CreateTabDetails
    ): GeckoResult<GeckoSession> {
        val tab = tabManager.createTab(extensionManager, this, createDetails.cookieStoreId)
        return GeckoResult.fromValue(tab)
    }

    override fun onUpdateTab(
        extension: WebExtension,
        session: GeckoSession,
        details: WebExtension.UpdateTabDetails
    ): GeckoResult<AllowOrDeny> {
        if (session is TabSession) {
            tabManager.setCurrentTab(session)
        }
        return GeckoResult.fromValue(AllowOrDeny.ALLOW)
    }

    override fun onCloseTab(source: WebExtension?, session: GeckoSession): GeckoResult<AllowOrDeny> {
        if (session is TabSession) {
            tabManager.closeTab(session)
        }
        return GeckoResult.fromValue(AllowOrDeny.ALLOW)
    }

    override fun close() {
        runtime.shutdown()
        tabManager.close()
    }

    fun createNewTab() {
        val tab = tabManager.createTab(extensionManager, this, null)
        tabManager.setCurrentTab(tab)
    }
}
