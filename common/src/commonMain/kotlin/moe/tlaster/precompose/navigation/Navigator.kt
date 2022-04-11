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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow

/**
 * Creates a [Navigator] that controls the [NavHost].
 *
 * @see NavHost
 */
@Composable
fun rememberNavController(): NavController {
    return remember { NavController() }
}

class NavController {
    // FIXME: 2021/4/1 Temp workaround for deeplink
    private var pendingNavigation: String? = null
    internal var stackManager: RouteStackManager? = null
        set(value) {
            field = value
            value?.let {
                pendingNavigation?.let { it1 -> it.navigate(it1) }
            }
        }

    val backQueue: List<BackStackEntry>
        get() = stackManager?.backStacks?.mapNotNull { it.currentEntry } ?: emptyList()

    val currentDestination: BackStackEntry?
        get() = stackManager?.currentEntry

    fun getBackStackEntry(route: String): BackStackEntry? {
        return stackManager?.getBackStackEntry(route)
    }

    /**
     * Navigate to a route in the current RouteGraph.
     *
     * @param route route for the destination
     * @param options navigation options for the destination
     */
    fun navigate(route: String, options: NavOptions? = null) {
        stackManager?.navigate(route, options) ?: run {
            pendingNavigation = route
        }
    }

    suspend fun navigateForResult(route: String, options: NavOptions? = null): Any? {
        stackManager?.navigate(route, options) ?: run {
            pendingNavigation = route
            return null
        }
        val currentEntry = stackManager?.currentEntry ?: return null
        return stackManager?.waitingForResult(currentEntry)
    }

    /**
     * Attempts to navigate up in the navigation hierarchy. Suitable for when the
     * user presses the "Up" button marked with a left (or start)-facing arrow in the upper left
     * (or starting) corner of the app UI.
     */
    fun goBack() {
        stackManager?.goBack()
    }

    fun goBackWith(result: Any? = null) {
        stackManager?.goBack(result)
    }

    /**
     * Compatibility layer for Jetpack Navigation
     */
    fun popBackStack() {
        goBack()
    }

    /**
     * Check if navigator can navigate up
     */
    val canGoBack: Boolean
        get() = stackManager?.canGoBack ?: false
}

@Composable
fun NavController.currentBackStackEntryAsState(): State<BackStackEntry?> {
    val currentNavBackStackEntry = remember { mutableStateOf(stackManager?.currentEntry) }
    LaunchedEffect(this) {
        snapshotFlow { stackManager?.currentEntry }.collect {
            currentNavBackStackEntry.value = it
        }
    }
    return currentNavBackStackEntry
}
