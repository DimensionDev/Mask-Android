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
package com.dimension.maskbook.labs

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.labs.data.JSMethod
import com.dimension.maskbook.labs.repository.AppRepository
import com.dimension.maskbook.labs.repository.IAppRepository
import com.dimension.maskbook.labs.ui.scenes.generatedRoute
import com.dimension.maskbook.labs.ui.tab.LabsTabScreen
import com.dimension.maskbook.labs.viewmodel.LabsViewModel
import com.dimension.maskbook.labs.viewmodel.MarketTrendSettingsViewModel
import com.dimension.maskbook.labs.viewmodel.PluginSettingsViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

object LabsSetup : ModuleSetup {

    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialNavigationApi::class
    )
    override fun NavGraphBuilder.route(navController: NavController, onFinish: () -> Unit) {
        generatedRoute(navController, onFinish = onFinish)
    }

    override fun dependencyInject() = module {
        single<IAppRepository> { AppRepository(get()) }
        single { JSMethod(get()) }

        single { LabsTabScreen() } bind TabScreen::class

        viewModel { LabsViewModel(get(), get()) }
        viewModel { PluginSettingsViewModel(get(), get()) }
        viewModel { MarketTrendSettingsViewModel(get()) }
    }

    override fun onExtensionReady() {
        KoinPlatformTools.defaultContext().get().get<IAppRepository>().init()
    }
}
