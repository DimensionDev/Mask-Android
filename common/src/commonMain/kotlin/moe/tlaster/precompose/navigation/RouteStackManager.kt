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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import moe.tlaster.precompose.lifecycle.Lifecycle
import moe.tlaster.precompose.lifecycle.LifecycleObserver
import moe.tlaster.precompose.lifecycle.LifecycleOwner
import moe.tlaster.precompose.navigation.bottomsheet.SheetContentHost
import moe.tlaster.precompose.ui.BackDispatcher
import moe.tlaster.precompose.ui.BackHandler
import moe.tlaster.precompose.viewmodel.ViewModelStore
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterialApi::class)
@Stable
internal class RouteStackManager(
    private val routeGraph: RouteGraph,
    private val stateHolder: SaveableStateHolder,
    private val sheetState: ModalBottomSheetState,
) : LifecycleObserver, BackHandler {
    // FIXME: 2021/4/1 Temp workaround for deeplink
    private var pendingNavigation: String? = null

    private val _suspendResult = linkedMapOf<BackStackEntry, Continuation<Any?>>()

    private var stackEntryId = Long.MIN_VALUE
    private var routeStackId = Long.MIN_VALUE

    var backDispatcher: BackDispatcher? = null
        set(value) {
            field?.unregister(this)
            field = value
            value?.register(this)
        }

    var lifeCycleOwner: LifecycleOwner? = null
        set(value) {
            field?.lifecycle?.removeObserver(this)
            field = value
            value?.lifecycle?.addObserver(this)
        }

    private var viewModel: NavControllerViewModel? = null

    private val _backStacks = mutableStateListOf<RouteStack>()

    internal val backStacks: List<RouteStack>
        get() = _backStacks

    internal val currentStack: RouteStack?
        get() = _backStacks.lastOrNull()

    internal val currentEntry: BackStackEntry?
        get() = currentStack?.currentEntry

    val canGoBack: Boolean
        get() = currentStack?.canGoBack == true || _backStacks.size > 1

    val isPop = mutableStateOf(false)

    internal val sheetContent: @Composable ColumnScope.() -> Unit = @Composable {
        // val columnScope = this
        val saveableStateHolder = rememberSaveableStateHolder()

        val currentEntry = currentStack?.currentBottomSheetEntry
        SheetContentHost(
            backStackEntry = currentEntry,
            sheetState = sheetState,
            stateHolder = saveableStateHolder,
            onSheetShown = {

            },
            onSheetDismissed = {
                goBack()
            }
        )
    }

    internal fun getRouteStack(route: String): RouteStack? {
        val matchResult = routeGraph.findRoute(route) ?: return null
        return _backStacks.find { it.hasRoute(matchResult.route.route) }
    }

    internal fun setViewModelStore(viewModelStore: ViewModelStore) {
        if (viewModel != NavControllerViewModel.create(viewModelStore)) {
            viewModel = NavControllerViewModel.create(viewModelStore)
        }
    }

    fun navigate(route: String, options: NavOptions? = null) {
        val vm = viewModel ?: run {
            pendingNavigation = route
            return
        }
        isPop.value = false

        val matchResult = routeGraph.findRoute(route)
        checkNotNull(matchResult) {
            "RouteStackManager: navigate target $route not found"
        }
        require(matchResult.route is ComposeRoute) {
            "RouteStackManager: navigate target $route is not ComposeRoute"
        }

        val query = route.substringAfter('?', "")
        fun newEntry(route: ComposeRoute): BackStackEntry {
            return BackStackEntry(
                id = stackEntryId++,
                route = route,
                pathMap = matchResult.pathMap,
                queryString = query.takeIf { it.isNotEmpty() }?.let {
                    QueryString(it)
                },
                viewModel = vm,
            )
        }

        fun newStack(
            entry: BackStackEntry,
            navTransition: NavTransition? = null,
            stackRoute: String? = null,
        ): RouteStack {
            return RouteStack(
                id = routeStackId++,
                topEntry = entry,
                navTransition = navTransition,
                stackRoute = stackRoute,
                viewModel = vm,
            )
        }

        var launchSingleTopSuccess = false
        if (options?.launchSingleTop == true && matchResult.route is SceneRoute) {
            _backStacks.firstOrNull { it.hasRoute(matchResult.route.route) }
                ?.let {
                    _backStacks.remove(it)
                    _backStacks.add(it)
                    launchSingleTopSuccess = true
                }
        }

        if (!launchSingleTopSuccess) {
            when (val matchRoute = matchResult.route) {
                is SceneRoute -> {
                    val entry = newEntry(matchRoute)
                    val stack = newStack(entry, matchRoute.navTransition)
                    _backStacks.add(stack)
                }
                is DialogRoute,
                is BottomSheetRoute -> {
                    val entry = newEntry(matchRoute)
                    currentStack?.entries?.add(entry)
                }
                is NavigationRoute -> {
                    val currentStack = currentStack

                    val stack = if (currentStack?.stackRoute == matchRoute.route) {
                        currentStack
                    } else {
                        val initialEntry = newEntry(matchRoute.initialRoute)
                        val stack = newStack(
                            initialEntry,
                            navTransition = matchRoute.initialRoute.navTransition,
                            stackRoute = matchRoute.route,
                        )
                        _backStacks.add(stack)
                        stack
                    }

                    if (route != matchRoute.route) {
                        val childMatchResult = matchRoute.graph.findRoute(route)
                        requireNotNull(childMatchResult) {
                            "RouteStackManager: child navigate target $route not found"
                        }
                        require(childMatchResult.route is ComposeRoute) {
                            "RouteStackManager: child navigate target $route is not ComposeRoute"
                        }

                        if (childMatchResult.route != matchRoute.initialRoute) {
                            stack.entries.add(newEntry(childMatchResult.route))
                        }
                    }
                }
            }
        }

        if (options?.popUpTo != null && matchResult.route is SceneRoute) {
            val index = _backStacks.indexOfLast { it.hasRoute(options.popUpTo.route) }
            if (index != -1 && index != _backStacks.lastIndex) {
                _backStacks.removeRange(
                    if (options.popUpTo.inclusive) index else index + 1,
                    _backStacks.lastIndex,
                )
            } else if (options.popUpTo.route.isEmpty()) {
                _backStacks.removeRange(0, _backStacks.lastIndex)
            }
        }
    }

    fun goBack(
        route: String? = null,
        inclusive: Boolean = false,
        result: Any? = null,
    ): Boolean {
        if (!canGoBack) {
            return false
        }
        isPop.value = true

        if (!route.isNullOrEmpty()) {
            val matchResult = routeGraph.findRoute(route)
            if (matchResult != null) {
                val index = _backStacks.indexOfLast { it.hasRoute(matchResult.route.route) }
                if (index != -1) {
                    _backStacks.removeRange(
                        if (inclusive) index else index + 1,
                        _backStacks.lastIndex,
                    )
                    return true
                }
            }
        }

        when {
            currentStack?.canGoBack == true -> {
                currentStack?.goBack()
            }
            _backStacks.size > 1 -> {
                val stack = _backStacks.removeLast()
                val entry = stack.currentEntry
                stateHolder.removeState(stack.id)
                stack.destroyAfterTransition()
                entry
            }
            else -> {
                null
            }
        }?.takeIf { backStackEntry ->
            _suspendResult.containsKey(backStackEntry)
        }?.let {
            _suspendResult.remove(it)?.resume(result)
        }
        return true
    }

    suspend fun waitingForResult(entry: BackStackEntry): Any? = suspendCoroutine {
        _suspendResult[entry] = it
    }

    override fun onStateChanged(state: Lifecycle.State) {
        when (state) {
            Lifecycle.State.Initialized -> Unit
            Lifecycle.State.Active -> currentStack?.onActive()
            Lifecycle.State.InActive -> currentStack?.onInActive()
            Lifecycle.State.Destroyed -> {
                _backStacks.forEach {
                    it.onDestroyed()
                }
                _backStacks.clear()
            }
        }
    }

    internal fun indexOf(stack: RouteStack): Int {
        return _backStacks.indexOf(stack)
    }

    override fun handleBackPress(): Boolean {
        return goBack()
    }

    fun navigateInitial(initialRoute: String) {
        navigate(initialRoute)
        pendingNavigation?.let {
            navigate(it)
        }
    }
}
