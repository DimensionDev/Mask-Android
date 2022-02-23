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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import org.json.JSONObject
import org.mozilla.geckoview.GeckoView

@Composable
fun rememberGeckoViewController(): GeckoViewController {
    return remember { GeckoViewController() }
}

class GeckoViewController internal constructor() {
    internal var geckoView: GeckoView? = null
    internal var engine: GeckoEngine? = null
    val canGoBack by lazy {
        engine.currentTab.flatMapLatest {
            it?.canGoBack ?: flowOf(false)
        }
    }
    val canGoForward by lazy {
        engine.currentTab.flatMapLatest {
            it?.canGoForward ?: flowOf(false)
        }
    }
    val message by lazy {
        engine.messageFlow
    }
    val tabCount by lazy {
        engine.tabCount
    }
    val currentTab by lazy {
        engine.currentTab
    }

    fun goBack(): Boolean {
        return engine?.goBack() ?: false
    }
    fun newTab() {
        engine?.createNewTab()
    }

    fun loadUrl(url: String) {
        engine?.loadUrl(url)
    }

    fun sendMessage(message: JSONObject) {
        engine?.sendMessage(message)
    }
}

@Composable
fun GeckoView(
    modifier: Modifier = Modifier,
    controller: GeckoViewController
) {
    val context = LocalContext.current
    val engine = remember {
        GeckoEngine(context).apply {
            controller.engine = this
        }
    }
    val session by engine.currentTab.collectAsState(initial = null)
    DisposableEffect(Unit) {
        onDispose {
            engine.close()
        }
    }
    AndroidView(
        modifier = modifier,
        factory = {
            GeckoView(it).also { geckoView ->
                controller.geckoView = geckoView
            }
        },
        update = { geckoView ->
            val previousSession = geckoView.session
            if (previousSession != session) {
                if (previousSession != null) {
                    geckoView.releaseSession()
                    engine.runtime.webExtensionController.setTabActive(previousSession, false)
                }
                session?.let { session ->
                    engine.runtime.webExtensionController.setTabActive(session, true)
                    geckoView.setSession(session)
                }
            }
        }
    )
}
