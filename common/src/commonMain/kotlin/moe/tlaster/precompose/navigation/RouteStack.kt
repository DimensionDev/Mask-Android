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

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import moe.tlaster.precompose.lifecycle.Lifecycle
import moe.tlaster.precompose.lifecycle.LifecycleOwner
import moe.tlaster.precompose.lifecycle.LifecycleRegistry
import moe.tlaster.precompose.viewmodel.ViewModelStore
import moe.tlaster.precompose.viewmodel.ViewModelStoreOwner

@Stable
class RouteStack internal constructor(
    val id: Long,
    val topEntry: BackStackEntry,
    val entries: SnapshotStateList<BackStackEntry> = mutableStateListOf(),
    val navTransition: NavTransition? = null,
    internal val stackRoute: String? = null,
    internal val viewModel: NavControllerViewModel,
) : ViewModelStoreOwner, LifecycleOwner {

    private var destroyAfterTransition = false

    override val viewModelStore: ViewModelStore
        get() = viewModel.get(id = id)

    private val lifecycleRegistry: LifecycleRegistry by lazy {
        LifecycleRegistry()
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    val currentEntry: BackStackEntry
        get() = entries.lastOrNull() ?: topEntry

    val canGoBack: Boolean
        get() = entries.isNotEmpty()

    internal val currentDialogEntry: BackStackEntry?
        get() = entries.lastOrNull { it.route is DialogRoute }

    internal val currentBottomSheetEntry: BackStackEntry?
        get() = entries.lastOrNull { it.route is BottomSheetRoute }

    fun goBack(): BackStackEntry {
        return entries.removeLast().also {
            it.destroy()
        }
    }

    fun onActive() {
        lifecycleRegistry.currentState = Lifecycle.State.Active
        currentEntry.active()
    }

    fun onInActive() {
        lifecycleRegistry.currentState = Lifecycle.State.InActive
        currentEntry.inActive()
        if (destroyAfterTransition) {
            onDestroyed()
        }
    }

    fun destroyAfterTransition() {
        destroyAfterTransition = true
    }

    fun onDestroyed() {
        lifecycleRegistry.currentState = Lifecycle.State.Destroyed
        entries.forEach { it.destroy() }
        entries.clear()
        viewModel.clear(id)
    }

    fun hasRoute(route: String): Boolean {
        return topEntry.route.route == route || entries.any { it.route.route == route }
            || stackRoute == route
    }
}
