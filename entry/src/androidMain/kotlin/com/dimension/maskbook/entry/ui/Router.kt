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
package com.dimension.maskbook.entry.ui

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.dimension.maskbook.common.CommonSetup
import com.dimension.maskbook.common.registerRoute
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.DeeplinkNavigateArgs
import com.dimension.maskbook.common.route.Navigator
import com.dimension.maskbook.common.route.RouteNavigateArgs
import com.dimension.maskbook.common.ui.widget.RouteHost
import com.dimension.maskbook.common.ui.widget.rememberMaskBottomSheetNavigator
import com.dimension.maskbook.entry.BuildConfig
import com.dimension.maskbook.entry.EntrySetup
import com.dimension.maskbook.entry.repository.EntryRepository
import com.dimension.maskbook.entry.route.EntryRoute
import com.dimension.maskbook.extension.ExtensionSetup
import com.dimension.maskbook.labs.LabsSetup
import com.dimension.maskbook.persona.PersonaSetup
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.setting.SettingSetup
import com.dimension.maskbook.wallet.WalletSetup
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import kotlinx.coroutines.flow.firstOrNull
import org.koin.mp.KoinPlatformTools

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Router(
    startDestination: String,
) {
    val bottomSheetNavigator = rememberMaskBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    LaunchedEffect(Unit) {
        val initialRoute = getInitialRoute()
        navController.navigate(initialRoute) {
            popUpTo(startDestination) {
                inclusive = true
            }
        }
        // ensure the initial route is set before handle deep links
        Navigator.navigateEvent.collect {
            it.getContentIfNotHandled()?.let { it1 ->
                when (it1) {
                    is DeeplinkNavigateArgs -> {
                        navController.navigate(Uri.parse(it1.url))
                    }
                    is RouteNavigateArgs -> navController.navigate(it1.route)
                }
            }
        }
    }

    RouteHost(
        bottomSheetNavigator = bottomSheetNavigator,
        navController = navController,
        startDestination = startDestination,
    ) {
        CommonSetup.registerRoute(navController = navController)
        EntrySetup.registerRoute(navController = navController)
        WalletSetup.registerRoute(navController = navController)
        LabsSetup.registerRoute(navController = navController)
        PersonaSetup.registerRoute(navController = navController)
        SettingSetup.registerRoute(navController = navController)
        ExtensionSetup.registerRoute(navController = navController)
    }
    if (BuildConfig.DEBUG) {
        ComposeDebugTool(navController)
    }
}

private suspend fun getInitialRoute(): String {
    val repository = KoinPlatformTools.defaultContext().get().get<EntryRepository>()
    val shouldShowEntry = repository.shouldShowEntry.firstOrNull() ?: true
    if (shouldShowEntry) {
        return EntryRoute.Intro
    }
    val hasPersona = KoinPlatformTools.defaultContext().get().get<PersonaServices>().hasPersona()
    return if (hasPersona) {
        CommonRoute.Main.Home(CommonRoute.Main.Tabs.Persona)
    } else {
        PersonaRoute.Register.Init
    }
}
