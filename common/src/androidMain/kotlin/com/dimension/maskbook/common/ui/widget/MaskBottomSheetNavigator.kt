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

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class,)
@Composable
fun rememberMaskBottomSheetNavigator(
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    skipHalfExpanded: Boolean = true,
): BottomSheetNavigator {
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec
    )

    if (skipHalfExpanded) {
        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.isAnimationRunning }
                .collect {
                    with(sheetState) {
                        val isOpening = currentValue == ModalBottomSheetValue.Hidden && targetValue == ModalBottomSheetValue.HalfExpanded
                        val isClosing = currentValue == ModalBottomSheetValue.Expanded && targetValue == ModalBottomSheetValue.HalfExpanded
                        when {
                            isOpening -> animateTo(ModalBottomSheetValue.Expanded)
                            isClosing -> animateTo(ModalBottomSheetValue.Hidden)
                        }
                    }
                }
        }
    }

    return remember(sheetState) {
        BottomSheetNavigator(sheetState = sheetState)
    }
}
