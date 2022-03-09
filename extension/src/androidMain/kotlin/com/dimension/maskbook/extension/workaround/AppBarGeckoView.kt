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

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import mozilla.components.browser.engine.gecko.GeckoEngineView

class AppBarGeckoView(context: Context) : CustomLayout(context), NestedScrollingParent3 {

    private val composeView = ComposeView(context).autoAddView()
    val geckoEngineView = GeckoEngineView(context).autoAddView()

    private var offsetY = 0
    private var headerHeight = 0

    private val mParentHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        onNestedScrollInternal(dyUnconsumed, consumed)
    }

    private fun onNestedScrollInternal(dyUnconsumed: Int, consumed: IntArray?) {
        if (dyUnconsumed < 0) scrollDown(dyUnconsumed, consumed)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        onNestedScrollInternal(dyUnconsumed, null)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0) scrollUp(dy, consumed)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        onNestedScrollAccepted(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(target: View) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        onNestedScrollInternal(dyUnconsumed, null)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    private fun scrollDown(dyUnconsumed: Int, consumed: IntArray?) {
        scrollHeader(dyUnconsumed, consumed)
    }

    private fun scrollUp(dy: Int, consumed: IntArray) {
        scrollHeader(dy, consumed)
    }

    private fun scrollHeader(y: Int, consumed: IntArray?) {
        val oldOffsetY = offsetY

        offsetY += y
        if (offsetY < 0) offsetY = 0
        else if (offsetY > headerHeight) offsetY = headerHeight

        if (oldOffsetY != offsetY) {
            composeView.scrollTo(0, offsetY)
            post { requestLayout() }

            consumed?.set(1, oldOffsetY - offsetY)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val parentView = MeasureSpec.getSize(heightMeasureSpec)
        composeView.autoMeasure()
        geckoEngineView.autoMeasure(
            heightMeasureSpec = (parentView - composeView.height + offsetY).toExactlyMeasureSpec()
        )
        headerHeight = composeView.measuredHeight
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        composeView.layout(0, 0)
        geckoEngineView.layout(0, composeView.height - offsetY)
    }

    fun setAppBar(content: @Composable () -> Unit) {
        composeView.setContent(content)
    }
}

abstract class CustomLayout(context: Context) : ViewGroup(context) {

    companion object {
        const val WRAP_CONTENT = MarginLayoutParams.WRAP_CONTENT
        const val MATCH_PARENT = MarginLayoutParams.MATCH_PARENT
    }

    protected fun <T : View> T.autoAddView(
        width: Int = MATCH_PARENT,
        height: Int = WRAP_CONTENT,
        block: T.(LayoutParams) -> Unit = {}
    ): T = apply {
        val mLayoutParams = LayoutParams(width, height)
        block(mLayoutParams)
        layoutParams = mLayoutParams
        this@CustomLayout.addView(this)
    }

    protected fun View.layout(x: Int, y: Int) = layout(
        x, y, x + measuredWidth, y + measuredHeight
    )

    protected fun View.autoMeasure(
        widthMeasureSpec: Int = defaultWidthMeasureSpec(parentView = this@CustomLayout),
        heightMeasureSpec: Int = defaultHeightMeasureSpec(parentView = this@CustomLayout)
    ) {
        measure(widthMeasureSpec, heightMeasureSpec)
    }

    protected fun View.defaultWidthMeasureSpec(parentView: ViewGroup): Int = when (layoutParams.width) {
        MATCH_PARENT -> (parentView.measuredWidth - paddingStart - paddingEnd).toExactlyMeasureSpec()
        WRAP_CONTENT -> (parentView.measuredWidth - paddingStart - paddingEnd).toAtMostMeasureSpec()
        0 -> throw IllegalAccessException("Need special treatment for $this")
        else -> layoutParams.width.toExactlyMeasureSpec()
    }

    protected fun View.defaultHeightMeasureSpec(parentView: ViewGroup): Int = when (layoutParams.height) {
        MATCH_PARENT -> (parentView.measuredHeight - paddingTop - paddingBottom).toExactlyMeasureSpec()
        WRAP_CONTENT -> (parentView.measuredHeight - paddingTop - paddingBottom).toAtMostMeasureSpec()
        0 -> throw IllegalAccessException("Need special treatment for $this")
        else -> layoutParams.height.toExactlyMeasureSpec()
    }

    protected fun Int.toExactlyMeasureSpec(): Int {
        return MeasureSpec.makeMeasureSpec(this, MeasureSpec.EXACTLY)
    }

    protected fun Int.toAtMostMeasureSpec(): Int {
        return MeasureSpec.makeMeasureSpec(this, MeasureSpec.AT_MOST)
    }
}
