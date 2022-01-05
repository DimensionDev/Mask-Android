package com.dimension.maskbook.wallet.ui.scenes.wallets.intro

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.CreateType
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.BalancesSceneType
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletBalancesScene
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletBalancesViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import org.koin.androidx.compose.getViewModel

@ExperimentalMaterialNavigationApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WalletIntroHost() {
    val rootNavController = LocalRootNavController.current
    val viewModel = getViewModel<WalletBalancesViewModel>()
    val dWebData by viewModel.dWebData.observeAsState(initial = null)
    val sceneType by viewModel.sceneType.observeAsState(initial = BalancesSceneType.Token)
    val currentWallet by viewModel.currentWallet.observeAsState(initial = null)
    val wallets by viewModel.wallets.observeAsState(initial = emptyList())
    if (currentWallet == null) {
        WalletIntroScene(
            onCreate = {
                rootNavController.navigate("WalletIntroHostLegal/${CreateType.CREATE}")
            },
            onImport = {
                rootNavController.navigate("WalletIntroHostLegal/${CreateType.IMPORT}")
            },
            onConnect = {/*TODO Logic: connect wallet*/ }
        )
    } else {
        dWebData?.let { dWebData ->
            currentWallet?.let { wallet ->
                WalletBalancesScene(
                    wallets = wallets,
                    currentWallet = wallet,
                    onWalletChanged = {
                        viewModel.setCurrentWallet(it)
                    },
                    onWalletMenuClicked = {
                        rootNavController.navigate("WalletBalancesMenu")
                    },
                    onWalletSwitchClicked = {
                        rootNavController.navigate("SwitchWallet")
                    },
                    onTokenDetailClicked = {
                        rootNavController.navigate("TokenDetail/${it.address}")
                    },
                    onReceiveClicked = {
                        rootNavController.navigate("WalletQrcode")
                    },
                    onSendClicked = {
                        rootNavController.navigate("SendTokenScene/eth")
                    },
                    sceneType = sceneType,
                    onSceneTypeChanged = {
                        viewModel.setSceneType(it)
                    },
                    chainType = dWebData.chainType
                )
            }
        }
    }
}
