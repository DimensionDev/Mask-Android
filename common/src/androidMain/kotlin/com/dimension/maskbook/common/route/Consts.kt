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
package com.dimension.maskbook.common.route

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

const val navigationComposeDialogPackage = "androidx.navigation.compose"
const val navigationComposeDialog = "dialog"

const val navigationComposeAnimComposablePackage = "com.dimension.maskbook.common.route"
const val navigationComposeAnimComposable = "composable"

const val navigationComposeModalComposablePackage = "com.dimension.maskbook.common.route"
const val navigationComposeModalComposable = "modalComposable"

const val navigationComposeBottomSheetPackage = "com.dimension.maskbook.common.route"
const val navigationComposeBottomSheet = "bottomSheet"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        content = content,
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.modalComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        content = content,
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
    )
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.bottomSheet(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit
) {
    bottomSheet(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        content = content
    )
}
