package com.dimension.maskbook.common

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.common.util.BiometricAuthenticator
import com.dimension.maskbook.wallet.viewmodel.wallets.BiometricEnableViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class CommonSetup : ModuleSetup {

    override fun NavGraphBuilder.route(navController: NavController) {

    }

    override fun dependencyInject() = module {
        single {
            BiometricAuthenticator()
        }

        viewModel { BiometricEnableViewModel(get(), get()) }
    }
}