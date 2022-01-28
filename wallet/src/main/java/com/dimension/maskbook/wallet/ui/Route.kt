/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.dimension.maskbook.wallet.navHostAnimationDurationMillis
import com.dimension.maskbook.wallet.route.backupRoute
import com.dimension.maskbook.wallet.route.labsRoute
import com.dimension.maskbook.wallet.route.mainRoute
import com.dimension.maskbook.wallet.route.registerRoute
import com.dimension.maskbook.wallet.route.settingsRoute
import com.dimension.maskbook.wallet.route.walletsRoute
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

val LocalRootNavController =
    staticCompositionLocalOf<NavHostController> { error("No NavHostController") }

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Route(
    startDestination: String = "Register",
    onBack: () -> Unit,
) {
    val navController = rememberAnimatedNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    CompositionLocalProvider(LocalRootNavController provides navController) {
        ModalBottomSheetLayout(
            bottomSheetNavigator,
            sheetBackgroundColor = MaterialTheme.colors.background,
        ) {
            AnimatedNavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = { _, _ ->
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(navHostAnimationDurationMillis))
                },
                exitTransition = { _, _ ->
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(navHostAnimationDurationMillis))
                },
                popEnterTransition = { _, _ ->
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(navHostAnimationDurationMillis))
                },
                popExitTransition = { _, _ ->
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(navHostAnimationDurationMillis))
                },
            ) {
                registerRoute(
                    navController = navController,
                )
                mainRoute(
                    navController = navController,
                    onBack = onBack,
                ) {
                    walletsRoute(navController = navController)
                    settingsRoute(navController = navController)
                    backupRoute(navController = navController)
                    labsRoute(navController = navController)
                }
            }
        }
    }
}
