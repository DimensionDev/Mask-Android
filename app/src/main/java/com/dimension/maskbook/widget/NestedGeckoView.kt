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
package com.dimension.maskbook.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.NestedScrollingChild
import androidx.core.view.ViewCompat
import org.mozilla.geckoview.GeckoView

class NestedGeckoView(
    context: Context,
    attrs: AttributeSet?
) : GeckoView(context, attrs), NestedScrollingChild {
    private var lastY = 0
    private val scrollOffset = IntArray(2)
    private val scrollConsumed = IntArray(2)
    private var nestedOffsetY = 0
    constructor(context: Context) : this(context, null)
    init {
        isNestedScrollingEnabled = true
    }
    override fun onTouchEvent(ev: MotionEvent): Boolean {

        if (ev.pointerCount == 1) {
            var returnValue = false
            val event = MotionEvent.obtain(ev)
            val action = event.actionMasked
            if (action == MotionEvent.ACTION_DOWN) {
                nestedOffsetY = 0
            }
            val eventY = event.y.toInt()
            event.offsetLocation(0f, nestedOffsetY.toFloat())
            when (action) {
                MotionEvent.ACTION_MOVE -> {
                    var deltaY = lastY - eventY
                    if (dispatchNestedPreScroll(0, deltaY, scrollConsumed, scrollOffset)) {
                        deltaY -= scrollConsumed[1]
                        lastY = eventY - scrollOffset[1]
                        event.offsetLocation(0f, -scrollOffset[1].toFloat())
                        nestedOffsetY += scrollOffset[1]
                    }
                    returnValue = super.onTouchEvent(event)

                    if (dispatchNestedScroll(0, scrollOffset[1], 0, deltaY, scrollOffset)) {
                        event.offsetLocation(0f, scrollOffset[1].toFloat())
                        nestedOffsetY += scrollOffset[1]
                        lastY -= scrollOffset[1]
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    returnValue = super.onTouchEvent(event)
                    lastY = eventY
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    returnValue = super.onTouchEvent(event)
                    stopNestedScroll()
                }
            }
            return returnValue
        }
        return super.onTouchEvent(ev)
    }
}
