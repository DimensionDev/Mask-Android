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
package com.dimension.maskbook.wallet.ui.scenes.wallets.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.viewmodel.BiometricEnableViewModel
import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.wallet.route.WalletRoute
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun WalletIntroHostLegalDispatcher(
    navController: NavController,
    originType: String?,
) {
    if (originType.isNullOrBlank()) {
        return
    }
    var enabled by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(originType) {
        delay(10)
        enabled = true
    }
    val type = originType.split(" ").first()
    val repo = get<SettingServices>()
    val password by repo.paymentPassword.observeAsState(initial = null)
    val enableBiometric by repo.biometricEnabled.observeAsState(initial = false)
    val shouldShowLegalScene by repo.shouldShowLegalScene.observeAsState(initial = true)
    val biometricEnableViewModel: BiometricEnableViewModel = getViewModel()
    val context = LocalContext.current

    val next: () -> Unit = {
        val route = if (password.isNullOrEmpty()) {
            WalletRoute.WalletIntroHostPassword(type)
        } else if (!enableBiometric && biometricEnableViewModel.isSupported(context)) {
            WalletRoute.WalletIntroHostFaceId(type)
        } else {
            WalletRoute.CreateOrImportWallet(type)
        }
        navController.navigate(route)
    }

    if (enabled) {
        enabled = false
        if (!shouldShowLegalScene) {
            next()
        } else {
            navController.navigate(WalletRoute.WalletIntroHostLegal(type))
        }
    }
}
