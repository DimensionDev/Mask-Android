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
package com.dimension.maskbook.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.retrofit.retrofit
import com.dimension.maskbook.setting.data.JSDataSource
import com.dimension.maskbook.setting.route.generatedRoute
import com.dimension.maskbook.setting.services.BackupServices
import org.koin.core.annotation.ComponentScan
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

@org.koin.core.annotation.Module
@ComponentScan
object SettingSetup : ModuleSetup {
    override fun NavGraphBuilder.route(navController: NavController) {
        generatedRoute(navController)
    }

    override fun dependencyInject(): Module = module {
        single<BackupServices> {
            retrofit("https://vaalh28dbi.execute-api.ap-east-1.amazonaws.com")
        }
    }

    override fun onExtensionReady() {
        KoinPlatformTools.defaultContext().get().get<JSDataSource>().initData()
    }
}
