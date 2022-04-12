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
package com.dimension.maskbook.common.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

private const val stiffnessTime = 220f

@Composable
fun RowScope.MaskAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(
        spring(stiffness = stiffnessTime)
    ) + expandHorizontally(
        spring(
            stiffness = stiffnessTime,
            visibilityThreshold = IntSize.VisibilityThreshold
        )
    ),
    exit: ExitTransition = fadeOut(
        spring(stiffness = stiffnessTime)
    ) + shrinkHorizontally(
        spring(
            stiffness = stiffnessTime,
            visibilityThreshold = IntSize.VisibilityThreshold
        )
    ),
    label: String = "MaskAnimatedVisibility",
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        label = label,
        content = content,
    )
}

@Composable
fun ColumnScope.MaskAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(
        spring(stiffness = stiffnessTime)
    ) + expandVertically(
        spring(
            stiffness = stiffnessTime,
            visibilityThreshold = IntSize.VisibilityThreshold
        )
    ),
    exit: ExitTransition = fadeOut(
        spring(stiffness = stiffnessTime)
    ) + shrinkVertically(
        spring(
            stiffness = stiffnessTime,
            visibilityThreshold = IntSize.VisibilityThreshold
        )
    ),
    label: String = "MaskAnimatedVisibility",
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        label = label,
        content = content,
    )
}
