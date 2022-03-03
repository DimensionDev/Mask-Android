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

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.retrofit.retrofit
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.setting.data.JSDataSource
import com.dimension.maskbook.setting.data.JSMethod
import com.dimension.maskbook.setting.data.SettingDataSource
import com.dimension.maskbook.setting.data.settingsDataStore
import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.setting.repository.BackupRepository
import com.dimension.maskbook.setting.repository.ISettingsRepository
import com.dimension.maskbook.setting.repository.SettingsRepository
import com.dimension.maskbook.setting.route.generatedRoute
import com.dimension.maskbook.setting.services.BackupServices
import com.dimension.maskbook.setting.ui.tab.SettingsTabScreen
import com.dimension.maskbook.setting.viewmodel.AppearanceSettingsViewModel
import com.dimension.maskbook.setting.viewmodel.BackupCloudExecuteViewModel
import com.dimension.maskbook.setting.viewmodel.BackupCloudViewModel
import com.dimension.maskbook.setting.viewmodel.BackupLocalViewModel
import com.dimension.maskbook.setting.viewmodel.BackupMergeConfirmViewModel
import com.dimension.maskbook.setting.viewmodel.BackupPasswordSettingsViewModel
import com.dimension.maskbook.setting.viewmodel.DataSourceSettingsViewModel
import com.dimension.maskbook.setting.viewmodel.EmailBackupViewModel
import com.dimension.maskbook.setting.viewmodel.EmailSetupViewModel
import com.dimension.maskbook.setting.viewmodel.LanguageSettingsViewModel
import com.dimension.maskbook.setting.viewmodel.PaymentPasswordSettingsViewModel
import com.dimension.maskbook.setting.viewmodel.PhoneBackupViewModel
import com.dimension.maskbook.setting.viewmodel.PhoneSetupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

object SettingSetup : ModuleSetup {
    override fun NavGraphBuilder.route(navController: NavController) {
        generatedRoute(navController)
    }

    override fun dependencyInject() = module {
        single<BackupServices> {
            retrofit("https://vaalh28dbi.execute-api.ap-east-1.amazonaws.com")
        }
        single<ISettingsRepository> {
            SettingsRepository(get(), get(), get(), get())
        }
        single { BackupRepository(get(), get<Context>().cacheDir, get<Context>().contentResolver) }
        single<SettingServices> { SettingServicesImpl(get(), get()) } bind com.dimension.maskbook.setting.export.BackupServices::class
        single { SettingsTabScreen() } bind TabScreen::class
        single { JSDataSource(get()) }
        single { JSMethod(get()) }
        single { SettingDataSource(get<Context>().settingsDataStore) }

        viewModel { LanguageSettingsViewModel(get()) }
        viewModel { AppearanceSettingsViewModel(get()) }
        viewModel { DataSourceSettingsViewModel(get()) }
        viewModel { PaymentPasswordSettingsViewModel(get()) }
        viewModel { BackupPasswordSettingsViewModel(get()) }
        viewModel { BackupLocalViewModel(get(), get()) }
        viewModel { EmailSetupViewModel(get(), get()) }
        viewModel { PhoneSetupViewModel(get(), get()) }
        viewModel { EmailBackupViewModel(get()) }
        viewModel { PhoneBackupViewModel(get()) }
        viewModel { (onDone: () -> Unit) ->
            BackupMergeConfirmViewModel(get(), get(), onDone)
        }
        viewModel { BackupCloudViewModel(get()) }
        viewModel { BackupCloudExecuteViewModel(get(), get(), get()) }
    }

    override fun onExtensionReady() {
        KoinPlatformTools.defaultContext().get().get<JSDataSource>().initData()
    }
}
