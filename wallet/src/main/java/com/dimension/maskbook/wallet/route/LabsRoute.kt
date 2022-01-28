/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.route

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.model.TransakConfig
import com.dimension.maskbook.wallet.ui.scenes.app.LabsTransakScene
import com.dimension.maskbook.wallet.ui.scenes.app.PluginSettingsScene
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import org.koin.androidx.compose.get

@ExperimentalAnimationApi
@ExperimentalMaterialNavigationApi
fun NavGraphBuilder.labsRoute(
    navController: NavController
) {
    composable("PluginSettings") {
        PluginSettingsScene(
            onBack = {
                navController.popBackStack()
            }
        )
    }

    composable(
        route = "LabsTransak"
    ) {
        val repo = get<IWalletRepository>()
        val currentWallet by repo.currentWallet.observeAsState(null)
        LabsTransakScene(
            onBack = { navController.popBackStack() },
            transakConfig = TransakConfig(
                isStaging = BuildConfig.DEBUG,
                walletAddress = currentWallet?.address ?: "",
                defaultCryptoCurrency = currentWallet?.tokens?.firstOrNull()?.tokenData?.symbol ?: "ETH",
            )
        )
    }
}
