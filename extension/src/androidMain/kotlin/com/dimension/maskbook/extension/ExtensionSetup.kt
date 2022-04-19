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
package com.dimension.maskbook.extension

import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.di.scope.appScope
import com.dimension.maskbook.common.di.scope.ioDispatcher
import com.dimension.maskbook.common.di.scope.repositoryCoroutineContext
import com.dimension.maskbook.common.ext.navigateToHome
import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.modalComposable
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.export.model.Site
import com.dimension.maskbook.extension.repository.ExtensionRepository
import com.dimension.maskbook.extension.route.ExtensionRoute
import com.dimension.maskbook.extension.ui.WebContentScene
import com.dimension.maskbook.extension.utils.BackgroundMessageChannel
import com.dimension.maskbook.extension.utils.ContentMessageChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.koin.dsl.module

object ExtensionSetup : ModuleSetup {
    override fun NavGraphBuilder.route(navController: NavController) {
        modalComposable(
            route = ExtensionRoute.WebContent.path,
            deepLinks = listOf(
                navDeepLink { uriPattern = Deeplinks.WebContent.path }
            ),
            arguments = listOf(
                navArgument("site") { type = NavType.StringType; nullable = true }
            ),
        ) {
            val backStackEntry by navController.currentBackStackEntryAsState()

            val site = it.arguments?.getString("site")?.let { Site.valueOf(it) }
            WebContentScene(
                onPersonaClicked = {
                    navController.navigateToHome()
                },
                enabledBack = backStackEntry == it,
                site = site,
            )
        }
    }

    override fun dependencyInject() = module {
        single {
            WebContentController(get(), get(appScope), get(ioDispatcher))
        }
        single {
            ExtensionRepository(
                get(repositoryCoroutineContext),
                get(),
            )
        }
        single {
            BackgroundMessageChannel(get())
        }
        single {
            ContentMessageChannel(get())
        }
        single<ExtensionServices> {
            ExtensionServicesImpl(get(), get(), get())
        }
    }

    override fun onExtensionReady(koin: Koin) {
        val appScope = koin.get<CoroutineScope>(appScope)
        val dispatcher = koin.get<CoroutineDispatcher>(ioDispatcher)

        appScope.launch(dispatcher) {
            koin.get<BackgroundMessageChannel>().startMessageCollect()
        }
        appScope.launch(dispatcher) {
            koin.get<ContentMessageChannel>().startMessageCollect()
        }
        appScope.launch(dispatcher) {
            koin.get<ExtensionRepository>().startCollect()
        }
    }
}
