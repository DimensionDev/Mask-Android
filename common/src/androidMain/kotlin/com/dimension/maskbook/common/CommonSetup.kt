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
package com.dimension.maskbook.common

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.common.di.module.coroutinesModule
import com.dimension.maskbook.common.di.scope.viewModelCoroutineContext
import com.dimension.maskbook.common.manager.ImageLoaderManager
import com.dimension.maskbook.common.manager.KeyStoreManager
import com.dimension.maskbook.common.util.BiometricAuthenticator
import com.dimension.maskbook.common.viewmodel.BiometricEnableViewModel
import com.dimension.maskbook.common.viewmodel.BiometricViewModel
import com.dimension.maskbook.common.viewmodel.SetUpPaymentPasswordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object CommonSetup : ModuleSetup {
    override fun NavGraphBuilder.route(navController: NavController) {
    }

    override fun dependencyInject() = module {
        coroutinesModule()

        single { BiometricAuthenticator() }
        single { KeyStoreManager(get()) }
        single { ImageLoaderManager(get()) }

        viewModel { BiometricEnableViewModel(get(viewModelCoroutineContext), get(), get()) }
        viewModel { BiometricViewModel(get(), get()) }
        viewModel { SetUpPaymentPasswordViewModel(get(viewModelCoroutineContext), get()) }
    }
}
