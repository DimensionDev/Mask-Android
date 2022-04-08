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

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.RouteBuilder

const val navigationComposeDialogPackage = "com.dimension.maskbook.common.route"
const val navigationComposeDialog = "dialog"

const val navigationComposeAnimComposablePackage = "com.dimension.maskbook.common.route"
const val navigationComposeAnimComposable = "composable"

const val navigationComposeModalComposablePackage = "com.dimension.maskbook.common.route"
const val navigationComposeModalComposable = "modalComposable"

const val navigationComposeBottomSheetPackage = "com.dimension.maskbook.common.route"
const val navigationComposeBottomSheet = "bottomSheet"

fun RouteBuilder.composable(
    route: String,
    // arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<String> = emptyList(),
    content: @Composable (BackStackEntry) -> Unit
) {
    scene(
        route = route,
        // arguments = arguments,
        deepLinks = deepLinks,
        content = content,
    )
}

fun RouteBuilder.modalComposable(
    route: String,
    // arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<String> = emptyList(),
    content: @Composable (BackStackEntry) -> Unit
) {
    scene(
        route = route,
        // arguments = arguments,
        deepLinks = deepLinks,
        content = content,
        // enterTransition = {
        //     slideInVertically { it }
        // },
        // exitTransition = null,
        // popEnterTransition = null,
        // popExitTransition = {
        //     slideOutVertically { it }
        // },
    )
}

fun RouteBuilder.bottomSheet(
    route: String,
    // arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<String> = emptyList(),
    content: @Composable (BackStackEntry) -> Unit
) {
    scene(
        route = route,
        // arguments = arguments,
        deepLinks = deepLinks,
        content = content
    )
}

fun RouteBuilder.dialog(
    route: String,
    // arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<String> = emptyList(),
    content: @Composable (BackStackEntry) -> Unit
) {
    dialog(
        route = route,
        // arguments = arguments,
        // deepLinks = deepLinks,
        content = content,
    )
}

fun RouteBuilder.navigation(
    route: String,
    startDestination: String,
    content: @Composable RouteBuilder.(BackStackEntry) -> Unit
) {
    // TODO
}
