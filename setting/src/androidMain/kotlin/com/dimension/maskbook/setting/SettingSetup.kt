package com.dimension.maskbook.setting

import androidx.navigation.NavController
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.retrofit.retrofit
import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.wallet.services.BackupServices
import org.koin.dsl.module

class SettingSetup : ModuleSetup {
    override fun route(navController: NavController) {

    }

    override fun dependencyInject() = module {
        single<BackupServices> {
            retrofit("https://vaalh28dbi.execute-api.ap-east-1.amazonaws.com")
        }
        single<SettingServices> { SettingServicesImpl() }
    }
}