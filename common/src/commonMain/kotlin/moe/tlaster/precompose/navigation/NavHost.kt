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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import moe.tlaster.precompose.ui.LocalBackDispatcherOwner
import moe.tlaster.precompose.ui.LocalLifecycleOwner
import moe.tlaster.precompose.ui.LocalViewModelStoreOwner

/**
 * Provides in place in the Compose hierarchy for self contained navigation to occur.
 *
 * Once this is called, any Composable within the given [RouteBuilder] can be navigated to from
 * the provided [RouteBuilder].
 *
 * The builder passed into this method is [remember]ed. This means that for this NavHost, the
 * contents of the builder cannot be changed.
 *
 * @param navController the Navigator for this host
 * @param initialRoute the route for the start destination
 * @param builder the builder used to construct the graph
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavHost(
    navController: NavController,
    initialRoute: String,
    navTransition: NavTransition = remember { NavTransition() },
    builder: RouteBuilder.() -> Unit,
) {
    NavHost(
        navController = navController,
        initialRoute = initialRoute,
        navTransition = navTransition,
        bottomSheetState = rememberModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            SwipeableDefaults.AnimationSpec
        ),
        builder = builder,
    )
}
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun NavHost(
    navController: NavController,
    initialRoute: String,
    bottomSheetState: ModalBottomSheetState,
    navTransition: NavTransition = remember { NavTransition() },
    builder: RouteBuilder.() -> Unit,
) {
    val stateHolder = rememberSaveableStateHolder()
    val manager = remember {
        val graph = RouteBuilder(initialRoute = initialRoute).apply(builder).build()
        RouteStackManager(graph, stateHolder, bottomSheetState).apply {
            navController.stackManager = this
        }
    }

    val lifecycleOwner = checkNotNull(LocalLifecycleOwner.current) {
        "NavHost requires a LifecycleOwner to be provided via LocalLifecycleOwner"
    }
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "NavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
    }
    val backDispatcher = LocalBackDispatcherOwner.current?.backDispatcher
    DisposableEffect(manager, lifecycleOwner, viewModelStoreOwner, backDispatcher) {
        manager.lifeCycleOwner = lifecycleOwner
        manager.setViewModelStore(viewModelStoreOwner.viewModelStore)
        manager.backDispatcher = backDispatcher
        onDispose {
            manager.lifeCycleOwner = null
        }
    }

    LaunchedEffect(manager, initialRoute) {
        manager.navigateInitial(initialRoute)
    }

    val currentStack = manager.currentStack
    if (currentStack != null) {

        val finalStackEnter: AnimatedContentScope<RouteStack>.() -> EnterTransition = {
            if (manager.isPop.value) {
                (targetState.navTransition ?: navTransition).popEnterTransition.invoke(this)
            } else {
                (targetState.navTransition ?: navTransition).enterTransition.invoke(this)
            }
        }
        val finalStackExit: AnimatedContentScope<RouteStack>.() -> ExitTransition = {
            if (manager.isPop.value) {
                (targetState.navTransition ?: navTransition).popExitTransition.invoke(this)
            } else {
                (targetState.navTransition ?: navTransition).exitTransition.invoke(this)
            }
        }

        val finalEntryEnter: AnimatedContentScope<BackStackEntry>.() -> EnterTransition = {
            if (manager.isPop.value) {
                (targetState.route.navTransition ?: navTransition).popEnterTransition.invoke(this)
            } else {
                (targetState.route.navTransition ?: navTransition).enterTransition.invoke(this)
            }
        }
        val finalEntryExit: AnimatedContentScope<BackStackEntry>.() -> ExitTransition = {
            if (manager.isPop.value) {
                (targetState.route.navTransition ?: navTransition).popExitTransition.invoke(this)
            } else {
                (targetState.route.navTransition ?: navTransition).exitTransition.invoke(this)
            }
        }

        AnimatedContent(
            currentStack,
            transitionSpec = {
                finalStackEnter(this) with finalStackExit(this)
            },
        ) { stack ->
            DisposableEffect(stack) {
                stack.onActive()
                onDispose {
                    stack.onInActive()
                }
            }

            @Composable
            fun initEntry(entry: BackStackEntry) {
                DisposableEffect(entry) {
                    entry.active()
                    onDispose {
                        entry.inActive()
                    }
                }

                stateHolder.SaveableStateProvider(entry.id) {
                    CompositionLocalProvider(
                        LocalViewModelStoreOwner provides entry,
                        LocalLifecycleOwner provides entry,
                    ) {
                        entry.route.content(entry)
                    }
                }
            }

            initEntry(stack.topEntry)

            val currentEntry = stack.currentDialogEntry
            if (currentEntry != null) {
                AnimatedContent(
                    currentEntry,
                    transitionSpec = {
                        finalEntryEnter(this) with finalEntryExit(this)
                    },
                ) { entry ->
                    initEntry(entry)
                }
            }
        }
    }
}
