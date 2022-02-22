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
import org.json.JSONObject
import org.mozilla.geckoview.AllowOrDeny
import org.mozilla.geckoview.BuildConfig
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.WebExtension
import org.mozilla.geckoview.WebExtensionController
import org.mozilla.geckoview.WebResponse

interface WebExtensionMessageHandler {
    fun onMessage(message: JSONObject)
}

internal class TabSession constructor(
    settings: GeckoSessionSettings? = null,
    extension: WebExtension? = null,
    extensionManager: ExtensionManager
) : GeckoSession(settings),
    GeckoSession.ContentDelegate,
    GeckoSession.ProgressDelegate,
    GeckoSession.NavigationDelegate {
    var fullScreen: Boolean = false
    var canGoBack: Boolean = false
    var canGoForward: Boolean = false

    init {
        contentDelegate = this
        progressDelegate = this
        navigationDelegate = this
        // promptDelegate = this // TODO
        // permissionDelegate = this // TODO
        // mediaDelegate = this // TODO
        // selectionActionDelegate = this // TODO
        if (extension != null) {
            webExtensionController.setActionDelegate(extension, extensionManager)
            webExtensionController.setTabDelegate(extension, extensionManager)
        }
    }

    override fun onFullScreen(session: GeckoSession, fullScreen: Boolean) {
        this.fullScreen = fullScreen
    }

    override fun exitFullScreen() {
        super.exitFullScreen()
        fullScreen = false
    }

    override fun onCloseRequest(session: GeckoSession) {
        // TODO
    }

    override fun onExternalResponse(session: GeckoSession, response: WebResponse) {
        // TODO
    }

    override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
        this.canGoBack = canGoBack
    }

    override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
        this.canGoForward = canGoForward
    }

    override fun onLoadRequest(
        session: GeckoSession,
        request: NavigationDelegate.LoadRequest
    ): GeckoResult<AllowOrDeny>? {
        // TODO
        return super.onLoadRequest(session, request)
    }

    override fun onSubframeLoadRequest(
        session: GeckoSession,
        request: NavigationDelegate.LoadRequest
    ): GeckoResult<AllowOrDeny> {
        // TODO
        return GeckoResult.fromValue(AllowOrDeny.ALLOW)
    }

    override fun onNewSession(session: GeckoSession, uri: String): GeckoResult<GeckoSession>? {
        // TODO
        return super.onNewSession(session, uri)
    }
}

internal class TabManager {
    private val tabs = arrayListOf<TabSession>()
    private var currentTabIndex = 0

    operator fun get(index: Int): TabSession? {
        return if (index < tabs.size) {
            tabs[index]
        } else {
            null
        }
    }

    fun getCurrentTab(): TabSession? {
        return get(currentTabIndex)
    }

    fun setCurrentTab(index: Int) {
        currentTabIndex = index
    }

    fun addTab(session: TabSession) {
        tabs.add(session)
    }

    fun removeTab(index: Int) {
        tabs.removeAt(index)
        if (currentTabIndex == index) {
            currentTabIndex = tabs.lastIndex
        }
    }

    fun setWebExtensionDelegates(
        extension: WebExtension,
        actionDelegate: WebExtension.ActionDelegate?,
        tabDelegate: WebExtension.SessionTabDelegate?
    ) {
        for (tab in tabs) {
            val sessionController = tab.webExtensionController
            sessionController.setActionDelegate(extension, actionDelegate)
            sessionController.setTabDelegate(extension, tabDelegate)
        }
    }
}

internal class ExtensionManager(
    private val handler: WebExtensionMessageHandler,
) : WebExtension.TabDelegate,
    WebExtension.ActionDelegate,
    WebExtension.MessageDelegate,
    WebExtension.SessionTabDelegate,
    WebExtension.PortDelegate {
    var extension: WebExtension? = null
        private set
    var port: WebExtension.Port? = null
        private set

    fun registerExtension(extension: WebExtension) {
        extension.setActionDelegate(this)
        extension.tabDelegate = this
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
            handler.onMessage(message)
        }
    }

    fun sendMessage(message: JSONObject) {
        port?.postMessage(message)
    }
}

class GeckoEngine(
    context: Context,
) : WebEngine, WebExtensionController.DebuggerDelegate, WebExtensionMessageHandler {
    private val runtime: GeckoRuntime
    private val extensionManager: ExtensionManager
    private val tabManager = TabManager()
    var webExtensionMessageHandler: WebExtensionMessageHandler? = null

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
        extensionManager = ExtensionManager(this)
        runtime.webExtensionController.setDebuggerDelegate(this)
    }

    override fun onExtensionListUpdated() {
        runtime.webExtensionController.list().accept { list ->
            list?.forEach {
                extensionManager.registerExtension(it)
                tabManager.setWebExtensionDelegates(
                    it,
                    extensionManager,
                    extensionManager
                )
            }
        }
    }

    override fun loadUrl(url: String) {
        TODO("Not yet implemented")
    }

    override fun ensureBuiltInExtension(id: String, uri: String) {
        TODO("Not yet implemented")
    }

    override fun goBack() {
    }

    fun sendMessage(message: JSONObject) {
        extensionManager.sendMessage(message)
    }

    override fun onMessage(message: JSONObject) {
        webExtensionMessageHandler?.onMessage(message)
    }
}
