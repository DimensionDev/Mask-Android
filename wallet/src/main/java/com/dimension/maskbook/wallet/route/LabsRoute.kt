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
        currentWallet?.let { wallet ->
            LabsTransakScene(
                onBack = { navController.popBackStack() },
                transakConfig = TransakConfig(
                    isStaging = BuildConfig.DEBUG,
                    walletAddress = wallet.address,
                    defaultCryptoCurrency = wallet.tokens.firstOrNull()?.tokenData?.symbol ?: "ETH",
                )
            )
        }
    }
}