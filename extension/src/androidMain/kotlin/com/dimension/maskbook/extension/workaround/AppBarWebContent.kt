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
package com.dimension.maskbook.extension.workaround

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.dimension.maskbook.common.gecko.WebContentController
import mozilla.components.browser.state.action.EngineAction
import mozilla.components.browser.state.helper.Target

@Composable
fun AppBarWebContent(
    controller: WebContentController,
    appBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedTab = Target.SelectedTab.observeAsComposableStateFrom(
        store = controller.store,
        observe = { tab ->
            // Render if the tab itself changed or when the state of the linked engine session changes
            arrayOf(
                tab?.id,
                tab?.engineState?.engineSession,
                tab?.engineState?.crashed,
                tab?.content?.firstContentfulPaint
            )
        }
    )

    AndroidView(
        modifier = modifier,
        factory = { context ->
            AppBarGeckoView(context).apply {
                setAppBar(appBar)
            }
        },
        update = { view ->
            val engineView = view.geckoEngineView

            val tab = selectedTab.value
            if (tab == null) {
                engineView.release()
            } else {
                val session = tab.engineState.engineSession
                if (session == null) {
                    // This tab does not have an EngineSession that we can render yet. Let's dispatch an
                    // action to request creating one. Once one was created and linked to this session, this
                    // method will get invoked again.
                    controller.store.dispatch(EngineAction.CreateEngineSessionAction(tab.id))
                } else {
                    engineView.render(session)
                }
            }
        }
    )
}
