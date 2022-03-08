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
package com.dimension.maskbook.extension.ui

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.withSign

@Composable
internal fun NestedScrollView(
    modifier: Modifier = Modifier,
    state: NestedScrollViewState = rememberNestedScrollViewState(),
    header: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier
            .nestedScroll(state.nestedScrollConnectionHolder),
        content = {
            Box {
                header.invoke()
            }
            Box {
                content.invoke()
            }
        },
    ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val headerPlaceable =
                measurables[0].measure(constraints.copy(maxHeight = Constraints.Infinity))
            headerPlaceable.place(0, state.offset.roundToInt())
            state.updateBounds(-(headerPlaceable.height.toFloat()))
            val size = (headerPlaceable.measuredHeight + state.offset).roundToInt()
            val contentPlaceable = measurables[1].measure(constraints.copy(maxHeight = constraints.maxHeight - size))
            contentPlaceable.place(
                0,
                size,
            )
        }
    }
}

/**
 * Create a [NestedScrollViewState] that is remembered across compositions.
 */
@Composable
internal fun rememberNestedScrollViewState(): NestedScrollViewState {
    val scope = rememberCoroutineScope()
    val saver = remember {
        NestedScrollViewState.Saver(scope = scope)
    }
    return rememberSaveable(
        saver = saver
    ) {
        NestedScrollViewState(scope = scope)
    }
}

/**
 * A state object that can be hoisted to observe scale and translate for [NestedScrollView].
 *
 * In most cases, this will be created via [rememberNestedScrollViewState].
 */
@Stable
internal class NestedScrollViewState(
    private val scope: CoroutineScope,
    initialOffset: Float = 0f,
    initialMaxOffset: Float = 0f,
) {
    companion object {
        fun Saver(
            scope: CoroutineScope,
        ): Saver<NestedScrollViewState, *> = listSaver(
            save = {
                listOf(it.offset, it._maxOffset.value)
            },
            restore = {
                NestedScrollViewState(
                    scope = scope,
                    initialOffset = it[0],
                    initialMaxOffset = it[1],
                )
            }
        )
    }

    /**
     * The maximum value for [NestedScrollView] Content to translate
     */
    @get:FloatRange(from = 0.0)
    val maxOffset: Float
        get() = _maxOffset.value.absoluteValue

    /**
     * The current value for [NestedScrollView] Content translate
     */
    @get:FloatRange(from = 0.0)
    val offset: Float
        get() = _offset.value

    internal val nestedScrollConnectionHolder = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            return takeIf {
                available.y < 0 && source == NestedScrollSource.Drag
            }?.let {
                Offset(0f, drag(available.y))
            } ?: Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return takeIf {
                available.y > 0 && source == NestedScrollSource.Drag
            }?.let {
                Offset(0f, drag(available.y))
            } ?: Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            return Velocity(0f, fling(available.y))
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            return Velocity(0f, fling(available.y))
        }
    }

    private var changes = 0f
    private var _offset = Animatable(initialOffset)
    private val _maxOffset = mutableStateOf(initialMaxOffset)

    private suspend fun snapTo(value: Float) {
        _offset.snapTo(value)
    }

    internal suspend fun fling(velocity: Float): Float {
        if (velocity == 0f || velocity > 0 && offset == 0f) {
            return velocity
        }
        val realVelocity = velocity.withSign(changes)
        changes = 0f
        return if (offset > _maxOffset.value && offset <= 0) {
            _offset.animateDecay(
                realVelocity,
                exponentialDecay()
            ).endState.velocity.let {
                if (offset == 0f) {
                    velocity
                } else {
                    it
                }
            }
        } else {
            0f
        }
    }

    internal fun drag(delta: Float): Float {
        return if (delta < 0 && offset > _maxOffset.value || delta > 0 && offset < 0f) {
            changes = delta
            scope.launch {
                snapTo((offset + delta).coerceIn(_maxOffset.value, 0f))
            }
            delta
        } else {
            0f
        }
    }

    internal fun updateBounds(maxOffset: Float) {
        _maxOffset.value = maxOffset
        _offset.updateBounds(maxOffset, 0f)
    }
}
