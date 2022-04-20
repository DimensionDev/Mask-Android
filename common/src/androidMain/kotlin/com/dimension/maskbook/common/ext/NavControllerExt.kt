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
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks

fun NavController.navigate(uri: Uri, builder: NavOptionsBuilder.() -> Unit) {
    navigate(uri, navOptions(builder))
}

fun NavController.navigateUri(uri: Uri, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(uri, navOptions(builder))
}

fun NavController.navigateWithPopSelf(route: String) {
    navigate(route) {
        currentDestination?.id?.let { popId ->
            popUpTo(popId) { inclusive = true }
        }
    }
}

fun NavController.navigateUriWithPopSelf(uri: String) {
    navigate(Uri.parse(uri)) {
        currentDestination?.id?.let { popId ->
            popUpTo(popId) { inclusive = true }
        }
    }
}

fun NavController.navigateToExtension(site: String? = null) {
    navigateUri(Uri.parse(Deeplinks.WebContent(site))) {
        launchSingleTop = true
    }
}

fun NavController.navigateToHome(initialRoute: String = CommonRoute.Main.Tabs.Persona) {
    navigateUri(Uri.parse(Deeplinks.Main.Home(initialRoute))) {
        launchSingleTop = true
        popUpTo(CommonRoute.Main.Home.path) {
            inclusive = false
        }
    }
}
