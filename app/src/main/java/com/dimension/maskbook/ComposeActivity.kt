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
package com.dimension.maskbook

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.plusAssign
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.decode.SvgDecoder
import com.dimension.maskbook.common.navHostAnimationDurationMillis
import com.dimension.maskbook.common.route
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.ui.LocalRootNavController
import com.dimension.maskbook.common.ui.theme.MaskTheme
import com.dimension.maskbook.labs.LabsSetup
import com.dimension.maskbook.persona.PersonaSetup
import com.dimension.maskbook.setting.SettingSetup
import com.dimension.maskbook.wallet.WalletSetup
import com.dimension.maskbook.wallet.route.mainRoute
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.route.registerRoute
import com.dimension.maskbook.wallet.route.walletsRoute
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

class ComposeActivity : AppCompatActivity() {
    companion object {
        object Destination {
            val register = WalletRoute.Register.Init
            val main = CommonRoute.Main.Home
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startDestination = intent.getStringExtra("startDestination") ?: Destination.register
        setContent {
            CompositionLocalProvider(
                LocalImageLoader provides ImageLoader.Builder(this).componentRegistry {
                    add(SvgDecoder(this@ComposeActivity))
                }.build()
            ) {
                MaskTheme {
                    App(
                        onBack = { finish() },
                        startDestination = startDestination,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun App(
    startDestination: String = ComposeActivity.Companion.Destination.register,
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
                mainRoute(onBack = onBack)
                WalletSetup.route(this, navController = navController, onBack = onBack)
                LabsSetup.route(this, navController = navController, onBack = onBack)
                PersonaSetup.route(this, navController = navController, onBack = onBack)
                SettingSetup.route(this, navController = navController, onBack = onBack)
            }
        }
    }
}
