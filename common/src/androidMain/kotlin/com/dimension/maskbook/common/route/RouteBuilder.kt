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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.NavTransition

fun RouteBuilder.composable(
    route: String,
    deepLinks: List<String> = emptyList(),
    content: @Composable (BackStackEntry) -> Unit
) {
    scene(
        route = route,
        deepLinks = deepLinks,
        content = content,
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun RouteBuilder.modalComposable(
    route: String,
    deepLinks: List<String> = emptyList(),
    content: @Composable (BackStackEntry) -> Unit
) {
    scene(
        route = route,
        deepLinks = deepLinks,
        content = content,
        navTransition = NavTransition(
            enterTransition = {
                slideInVertically { it }
            },
            exitTransition = NavTransition.NoneExit,
            popEnterTransition = NavTransition.NoneEnter,
            popExitTransition = {
                slideOutVertically { it }
            },
        )
    )
}

fun RouteBuilder.bottomSheet(
    route: String,
    deepLinks: List<String> = emptyList(),
    content: @Composable (BackStackEntry) -> Unit
) {
    bottomSheet(
        route = route,
        deepLinks = deepLinks,
        content = content
    )
}

fun RouteBuilder.dialog(
    route: String,
    deepLinks: List<String> = emptyList(),
    content: @Composable (BackStackEntry) -> Unit
) {
    dialog(
        route = route,
        deepLinks = deepLinks,
        content = content,
    )
}

fun RouteBuilder.navigation(
    route: String,
    startDestination: String,
    content: RouteBuilder.() -> Unit
) {
    // TODO
}
