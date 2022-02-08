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
import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.wallet.services.BackupServices
import com.dimension.maskbook.wallet.services.model.DownloadResponse
import com.dimension.maskbook.wallet.viewmodel.settings.AppearanceSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupCloudExecuteViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupCloudViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupLocalViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupMergeConfirmViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupPasswordSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.DataSourceSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.EmailBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.EmailSetupViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.LanguageSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.PaymentPasswordSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.PhoneBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.PhoneSetupViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.RemoteBackupRecoveryViewModelBase
// import com.dimension.maskbook.common.viewmodel.BiometricEnableViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object SettingSetup : ModuleSetup {
    override fun NavGraphBuilder.route(navController: NavController) {
    }

    override fun dependencyInject() = module {
        single<BackupServices> {
            retrofit("https://vaalh28dbi.execute-api.ap-east-1.amazonaws.com")
        }
        single<SettingServices> { SettingServicesImpl() }
        single {  }

        viewModel { LanguageSettingsViewModel(get()) }
        viewModel { AppearanceSettingsViewModel(get()) }
        viewModel { DataSourceSettingsViewModel(get()) }
        viewModel { PaymentPasswordSettingsViewModel(get()) }
        viewModel { BackupPasswordSettingsViewModel(get()) }
        viewModel { BackupLocalViewModel(get(), get()) }
        viewModel { (requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit) ->
            EmailSetupViewModel(
                requestNavigate = requestNavigate,
                backupRepository = get(),
                settingsRepository = get(),
            )
        }
        viewModel { (requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit) ->
            PhoneSetupViewModel(
                requestNavigate = requestNavigate,
                backupRepository = get(),
                settingsRepository = get()
            )
        }
        viewModel { (
            requestMerge: (target: DownloadResponse, email: String, code: String) -> Unit,
            next: (email: String, code: String) -> Unit,
        ) ->
            EmailBackupViewModel(get(), requestMerge, next)
        }
        viewModel { (
            requestMerge: (target: DownloadResponse, email: String, code: String) -> Unit,
            next: (email: String, code: String) -> Unit,
        ) ->
            PhoneBackupViewModel(get(), requestMerge, next)
        }
        viewModel { (onDone: () -> Unit) ->
            BackupMergeConfirmViewModel(get(), get(), onDone)
        }
        viewModel { BackupCloudViewModel(get()) }
        viewModel { BackupCloudExecuteViewModel(get(), get(), get()) }
        // viewModel { BiometricEnableViewModel(get(), get()) }
    }
}
