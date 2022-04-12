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
package com.dimension.maskbook.common.ext

import android.net.Uri
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import moe.tlaster.precompose.navigation.NavController
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo

class NavOptionsBuilder internal constructor() {

    var launchSingleTop = false

    private var popUpTo: PopUpTo? = null

    fun popUpTo(route: String, popUpBuilder: PopUpToBuilder.() -> Unit) {
        popUpTo = PopUpToBuilder().apply(popUpBuilder).build(route)
    }

    fun build(): NavOptions {
        return NavOptions(
            launchSingleTop = launchSingleTop,
            popUpTo = popUpTo,
        )
    }
}

class PopUpToBuilder internal constructor() {

    var inclusive = false

    fun build(route: String): PopUpTo {
        return PopUpTo(
            route = route,
            inclusive = inclusive
        )
    }
}

fun navOptions(builder: NavOptionsBuilder.() -> Unit): NavOptions {
    return NavOptionsBuilder().apply(builder).build()
}

fun NavController.navigate(route: String, builder: NavOptionsBuilder.() -> Unit) {
    navigate(route, navOptions(builder))
}

fun NavController.navigateUri(uri: Uri, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(uri.toString(), navOptions(builder))
}

fun NavController.navigateUri(uri: Uri, navOptions: NavOptions) {
    navigate(uri.toString(), navOptions)
}

fun NavController.navigateWithPopSelf(route: String) {
    navigate(route) {
        currentBackStackEntry?.route?.let { popRoute ->
            popUpTo(popRoute.route) { inclusive = true }
        }
    }
}

fun NavController.navigateUriWithPopSelf(uri: Uri) {
    navigateUri(uri) {
        currentBackStackEntry?.route?.let { popRoute ->
            popUpTo(popRoute.route) { inclusive = true }
        }
    }
}

fun NavController.navigateToExtension(site: String? = null) {
    navigate(Deeplinks.WebContent(site)) {
        launchSingleTop = true
        popUpTo(CommonRoute.Main.Home.path) {
            inclusive = true
        }
    }
}
