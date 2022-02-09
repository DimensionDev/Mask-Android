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
package com.dimension.maskbook.wallet.route

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.dimension.maskbook.wallet.ui.scenes.MainHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

@ExperimentalAnimationApi
@ExperimentalMaterialNavigationApi
fun NavGraphBuilder.mainRoute(
    navController: NavController,
    onBack: () -> Unit,
    builder: NavGraphBuilder.() -> Unit,
) {
    navigation(route = "Main", startDestination = "Home") {
        composable(
            "Home",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "maskwallet://Home/{tab}"
                }
            ),
            arguments = listOf(
                navArgument("tab") { type = NavType.StringType }
            )
        ) {
            MainHost(
                initialTab = it.arguments?.getString("tab").orEmpty(),
                onBack = onBack,
            )
        }
        builder()
    }
}
