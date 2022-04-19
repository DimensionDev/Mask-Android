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

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat.TYPE_TOUCH
import kotlinx.coroutines.runBlocking
import mozilla.components.browser.state.action.EngineAction
import mozilla.components.browser.state.helper.Target
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.Engine
import kotlin.math.roundToInt

@SuppressLint("ViewConstructor")
private class GeckoParent(
    context: Context,
    private val nestedScrollDispatcher: NestedScrollDispatcher,
) : FrameLayout(context), NestedScrollingParent3 {

    init {
        isNestedScrollingEnabled = true
    }
    private val helper by lazy {
        NestedScrollingParentHelper(this)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return true
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        helper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun getNestedScrollAxes(): Int {
        return helper.nestedScrollAxes
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        helper.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        val result = nestedScrollDispatcher.dispatchPostScroll(
            consumed = Offset(dxConsumed.toFloat(), dyConsumed.inv().toFloat()),
            available = Offset(dxUnconsumed.toFloat(), dyUnconsumed.inv().toFloat()),
            source = type.toNestedScrollSource()
        )
        consumed[0] = result.x.roundToInt()
        consumed[1] = result.y.roundToInt().inv()
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        val consumedX = if (consumed) velocityX else 0f
        val consumedY = if (consumed) velocityY else 0f
        val avaliableX = if (consumed) 0f else velocityX
        val avaliableY = if (consumed) 0f else velocityY
        val result = runBlocking {
            nestedScrollDispatcher.dispatchPostFling(
                Velocity(consumedX, (-consumedY)),
                Velocity(avaliableX, (-avaliableY)),
            )
        }
        return result.x != 0f || result.y != 0f
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        val result = runBlocking { nestedScrollDispatcher.dispatchPreFling(Velocity(velocityX, (-velocityY))) }
        return result.x != 0f || result.y != 0f
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val result =
            nestedScrollDispatcher.dispatchPreScroll(Offset(dx.toFloat(), dy.inv().toFloat()), type.toNestedScrollSource())
        consumed[0] = result.x.roundToInt()
        consumed[1] = result.y.roundToInt().inv()
    }
}

private fun Int.toNestedScrollSource(): NestedScrollSource = when (this) {
    TYPE_TOUCH -> NestedScrollSource.Drag
    else -> NestedScrollSource.Fling
}

@Composable
internal fun GeckoContent(
    modifier: Modifier = Modifier,
    engine: Engine,
    store: BrowserStore,
    target: Target,
    controller: WebContentViewController,
) {
    val nestedScrollDispatcher = remember { NestedScrollDispatcher() }
    val selectedTab = target.observeAsComposableStateFrom(
        store = store,
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

    val context = LocalContext.current
    val engineView = remember(context, engine) {
        engine.createView(context).apply {
            controller.view = this
        }
    }

    val parentView = remember(context, nestedScrollDispatcher) {
        GeckoParent(
            context = context,
            nestedScrollDispatcher = nestedScrollDispatcher
        )
    }

    DisposableEffect(engineView) {
        engineView as View

        parentView.addView(engineView)
        onDispose {
            parentView.removeView(engineView)
            engineView.release()
        }
    }

    val tab = selectedTab.value
    LaunchedEffect(tab) {
        if (tab == null) {
            engineView.release()
        } else {
            val session = tab.engineState.engineSession
            if (session == null) {
                // This tab does not have an EngineSession that we can render yet. Let's dispatch an
                // action to request creating one. Once one was created and linked to this session, this
                // method will get invoked again.
                store.dispatch(EngineAction.CreateEngineSessionAction(tab.id))
            } else {
                engineView.render(session)
            }
        }
    }

    AndroidView(
        modifier = modifier.nestedScroll(EmptyNestedScrollConnection, nestedScrollDispatcher),
        factory = { parentView },
        update = {}
    )
}

private object EmptyNestedScrollConnection : NestedScrollConnection
