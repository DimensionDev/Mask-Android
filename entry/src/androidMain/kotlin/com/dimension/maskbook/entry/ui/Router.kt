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
package com.dimension.maskbook.entry.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.plusAssign
import com.dimension.maskbook.common.navHostAnimationDurationMillis
import com.dimension.maskbook.common.route
import com.dimension.maskbook.common.ui.LocalRootNavController
import com.dimension.maskbook.common.ui.theme.modalScrimColor
import com.dimension.maskbook.common.ui.widget.rememberMaskBottomSheetNavigator
import com.dimension.maskbook.entry.EntrySetup
import com.dimension.maskbook.extension.ExtensionSetup
import com.dimension.maskbook.labs.LabsSetup
import com.dimension.maskbook.persona.PersonaSetup
import com.dimension.maskbook.setting.SettingSetup
import com.dimension.maskbook.wallet.WalletSetup
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Router(
    startDestination: String,
    onFinish: () -> Unit,
) {
    val navController = rememberAnimatedNavController()
    val bottomSheetNavigator = rememberMaskBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    CompositionLocalProvider(LocalRootNavController provides navController) {
        ModalBottomSheetLayout(
            bottomSheetNavigator,
            sheetBackgroundColor = MaterialTheme.colors.background,
            sheetShape = MaterialTheme.shapes.large.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp),
            ),
            scrimColor = MaterialTheme.colors.modalScrimColor,
        ) {
            AnimatedNavHost(
                navController = navController,
                startDestination = startDestination,
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
            ) {
                EntrySetup.route(this, navController = navController, onFinish = onFinish)
                WalletSetup.route(this, navController = navController, onFinish = onFinish)
                LabsSetup.route(this, navController = navController, onFinish = onFinish)
                PersonaSetup.route(this, navController = navController, onFinish = onFinish)
                SettingSetup.route(this, navController = navController, onFinish = onFinish)
                ExtensionSetup.route(this, navController = navController, onFinish = onFinish)
            }
        }
    }
}
