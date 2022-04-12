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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavController
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.rememberNavController
import moe.tlaster.precompose.navigation.NavTransition

private const val navHostAnimationDurationMillis = 320

@ExperimentalAnimationApi
@Composable
fun RouteHost(
    // bottomSheetNavigator: BottomSheetNavigator = rememberMaskBottomSheetNavigator(),
    navController: NavController = rememberNavController(),
    startDestination: String,
    builder: RouteBuilder.() -> Unit
) {
    // ModalBottomSheetLayout(
    //     // bottomSheetNavigator,
    //     sheetBackgroundColor = MaterialTheme.colors.background,
    //     sheetShape = MaterialTheme.shapes.large.copy(
    //         bottomStart = CornerSize(0.dp),
    //         bottomEnd = CornerSize(0.dp),
    //     ),
    //     scrimColor = MaterialTheme.colors.modalScrimColor,
    // ) {
    NavHost(
        navController = navController,
        initialRoute = startDestination,
        navTransition = NavTransition(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(
                        navHostAnimationDurationMillis
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(
                        navHostAnimationDurationMillis
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(
                        navHostAnimationDurationMillis
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(
                        navHostAnimationDurationMillis
                    )
                )
            },
        ),
        builder = builder,
    )
    // }
}
