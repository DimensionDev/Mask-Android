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
package moe.tlaster.precompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.compose.currentBackStackEntryAsState

actual typealias NavController = androidx.navigation.NavController
actual typealias BackStackEntry = androidx.navigation.NavBackStackEntry

actual typealias RouteBuilder = androidx.navigation.NavGraphBuilder

@Composable
actual fun NavController.currentBackStackEntryAsState(): State<BackStackEntry?> {
    return currentBackStackEntryAsState()
}
