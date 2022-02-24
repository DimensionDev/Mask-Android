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
import androidx.compose.runtime.Stable
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.tabs.TabsUseCases
import org.mozilla.geckoview.GeckoRuntime

@Stable
class WebContentState(
    private val context: Context
) {
    internal val store by lazy {
        BrowserStore()
    }
    private val runtime by lazy {
        GeckoRuntime.create(context)
    }
    internal val engine by lazy {
        GeckoEngine(context, runtime = runtime)
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
            onSuccess = {
                // TODO
            }
        )
    }
    fun newTab(url: String) = tabsUseCases.addTab(url)
    fun closeTab(id: String) = tabsUseCases.removeTab(id)
    fun switchTab(id: String) = tabsUseCases.selectTab(id)
    fun loadUrl(url: String) = sessionUseCases.loadUrl(url)
    fun refresh() = sessionUseCases.reload()
    fun goBack() = sessionUseCases.goBack()
    fun goForward() = sessionUseCases.goForward()
    val canGoBack get() = store.state.selectedTab?.content?.canGoBack ?: false
    val canGoForward get() = store.state.selectedTab?.content?.canGoForward ?: false
    val url get() = store.state.selectedTab?.content?.url ?: ""
    val title get() = store.state.selectedTab?.content?.title ?: ""
}
