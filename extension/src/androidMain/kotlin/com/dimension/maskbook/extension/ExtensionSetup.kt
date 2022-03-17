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
package com.dimension.maskbook.extension

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.export.model.Site
import com.dimension.maskbook.extension.repository.ExtensionRepository
import com.dimension.maskbook.extension.route.ExtensionRoute
import com.dimension.maskbook.extension.ui.WebContentScene
import com.dimension.maskbook.extension.utils.MessageChannel
import com.google.accompanist.navigation.animation.composable
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

object ExtensionSetup : ModuleSetup {
    @OptIn(ExperimentalAnimationApi::class)
    override fun NavGraphBuilder.route(navController: NavController) {
        composable(
            route = ExtensionRoute.WebContent.path,
            deepLinks = listOf(
                navDeepLink { uriPattern = Deeplinks.WebContent.path }
            ),
            arguments = listOf(
                navArgument("site") { type = NavType.StringType; nullable = true }
            ),
            exitTransition = {
                scaleOut(
                    targetScale = 0.9f,
                )
            },
            popExitTransition = null,
            popEnterTransition = {
                scaleIn(
                    initialScale = 0.9f,
                )
            }
        ) {
            val site = it.arguments?.getString("site")?.let { Site.valueOf(it) }
            WebContentScene(
                onPersonaClicked = {
                    navController.navigate(
                        Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)),
                        navOptions {
                            launchSingleTop = true
                            popUpTo(ExtensionRoute.WebContent.path)
                        },
                    )
                },
                site = site,
            )
        }
    }

    override fun dependencyInject() = module {
        single { WebContentController(get()) }
        single { ExtensionRepository(get()) }
        single<ExtensionServices> { ExtensionServicesImpl(get(), get()) }
        single { MessageChannel(get()) }
    }

    override fun onExtensionReady() {
        KoinPlatformTools.defaultContext().get().get<MessageChannel>().startMessageCollect()
    }
}
