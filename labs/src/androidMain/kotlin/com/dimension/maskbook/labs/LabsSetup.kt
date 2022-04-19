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

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.di.scope.appScope
import com.dimension.maskbook.common.di.scope.ioDispatcher
import com.dimension.maskbook.common.di.scope.preferenceCoroutineContext
import com.dimension.maskbook.common.di.scope.repositoryCoroutineContext
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.labs.data.JSMethod
import com.dimension.maskbook.labs.data.RedPacketMethod
import com.dimension.maskbook.labs.repository.AppRepository
import com.dimension.maskbook.labs.repository.IAppRepository
import com.dimension.maskbook.labs.repository.IPreferenceRepository
import com.dimension.maskbook.labs.repository.PreferenceRepository
import com.dimension.maskbook.labs.repository.labsDataStore
import com.dimension.maskbook.labs.ui.scenes.generatedRoute
import com.dimension.maskbook.labs.ui.tab.LabsTabScreen
import com.dimension.maskbook.labs.viewmodel.LabsViewModel
import com.dimension.maskbook.labs.viewmodel.LuckDropViewModel
import com.dimension.maskbook.labs.viewmodel.PluginSettingsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.dsl.bind
import org.koin.dsl.module

object LabsSetup : ModuleSetup {

    override fun NavGraphBuilder.route(navController: NavController) {
        generatedRoute(navController)
    }

    override fun dependencyInject() = module {
        single<IPreferenceRepository> {
            PreferenceRepository(
                get<Context>().labsDataStore,
                get(preferenceCoroutineContext),
            )
        }
        single<IAppRepository> {
            AppRepository(
                get(repositoryCoroutineContext),
                get(),
            )
        }
        single {
            JSMethod(get())
        }
        single {
            RedPacketMethod(get())
        }

        single { LabsTabScreen() } bind TabScreen::class

        viewModel { LabsViewModel(get(), get()) }
        viewModel { PluginSettingsViewModel(get(), get(), get()) }
        viewModel { (dataRaw: String, requestRaw: String?) -> LuckDropViewModel(dataRaw, requestRaw, get(), get()) }
    }

    override fun onExtensionReady(koin: Koin) {
        val appScope = koin.get<CoroutineScope>(appScope)
        val dispatcher = koin.get<CoroutineDispatcher>(ioDispatcher)

        appScope.launch(dispatcher) {
            koin.get<IAppRepository>().init()
        }
        appScope.launch(dispatcher) {
            koin.get<RedPacketMethod>().startCollect()
        }
    }
}
