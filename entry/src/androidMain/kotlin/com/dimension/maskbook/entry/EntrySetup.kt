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
package com.dimension.maskbook.entry

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.route.Navigator
import com.dimension.maskbook.entry.data.JSMethod
import com.dimension.maskbook.entry.repository.EntryRepository
import com.dimension.maskbook.entry.repository.entryDataStore
import com.dimension.maskbook.entry.ui.scene.generatedRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

object EntrySetup : ModuleSetup {
    override fun NavGraphBuilder.route(navController: NavController) {
        generatedRoute(navController)
    }

    override fun dependencyInject() = module {
        single { EntryRepository(get<Context>().entryDataStore) }
        single { JSMethod(get()) }
    }

    override fun onExtensionReady() {
        KoinPlatformTools.defaultContext().get().get<JSMethod>().apply {
            CoroutineScope(Dispatchers.IO).launch {
                launch {
                    merge(
                        openCreateWalletView(),
                        openDashboardView(),
                        openAppsView(),
                        openSettingsView(),
                    ).filter { uri ->
                        uri.isNotEmpty()
                    }.collect { uri ->
                        Navigator.deeplink(uri)
                    }
                }
            }
        }
    }
}
