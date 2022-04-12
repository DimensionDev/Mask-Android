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
import moe.tlaster.precompose.navigation.route.BottomSheetRoute
import moe.tlaster.precompose.navigation.route.ComposeRoute
import moe.tlaster.precompose.navigation.route.DialogRoute
import moe.tlaster.precompose.navigation.route.SceneRoute
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
    var backDispatcher: BackDispatcher? = null
        set(value) {
            field?.unregister(this)
            field = value
            value?.register(this)
        }
    private var stackEntryId = Long.MIN_VALUE
    private var routeStackId = Long.MIN_VALUE
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

    private val routeParser: RouteParser by lazy {
        RouteParser().apply {
            routeGraph.routes.asSequence()
                .map { route ->
                    RouteParser.expandOptionalVariables(route.route).let {
                        if (route is SceneRoute) {
                            it + route.deepLinks.flatMap {
                                RouteParser.expandOptionalVariables(it)
                            }
                        } else {
                            it
                        }
                    } to route
                }
                .flatMap { it.first.map { route -> route to it.second } }
                .forEach { insert(it.first, it.second) }
        }
    }

    private fun findRoute(route: String): RouteMatchResult? {
        val routePath = route.substringBefore('?')
        return routeParser.find(path = routePath)
    }

    internal fun getBackStackEntry(route: String): BackStackEntry? {
        return _backStacks.find { it.hasRoute(route) }?.currentEntry
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

        val matchResult = findRoute(route)
        checkNotNull(matchResult) { "RouteStackManager: navigate target $route not found" }
        require(matchResult.route is ComposeRoute) { "RouteStackManager: navigate target $route is not ComposeRoute" }

        val query = route.substringAfter('?', "")
        fun newEntry(): BackStackEntry {
            return BackStackEntry(
                id = stackEntryId++,
                route = matchResult.route,
                pathMap = matchResult.pathMap,
                queryString = query.takeIf { it.isNotEmpty() }?.let {
                    QueryString(it)
                },
                viewModel = vm,
            )
        }

        fun newTask(entry: BackStackEntry, navTransition: NavTransition? = null): RouteStack {
            return RouteStack(
                id = routeStackId++,
                topEntry = entry,
                navTransition = navTransition,
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
            val entry = newEntry()
            when (matchResult.route) {
                is SceneRoute -> {
                    _backStacks.add(
                        newTask(entry, matchResult.route.navTransition)
                    )
                }
                is DialogRoute,
                is BottomSheetRoute -> {
                    currentStack?.entries?.add(entry)
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
            val matchResult = findRoute(route)
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
