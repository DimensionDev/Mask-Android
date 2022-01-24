package com.dimension.maskbook.wallet.ui.scenes.wallets.intro

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.paging.compose.collectAsLazyPagingItems
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
fun WalletIntroHost(
    onBack: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val rootNavController = LocalRootNavController.current
    val viewModel = getViewModel<WalletBalancesViewModel>()
    val collectible = viewModel.collectible.collectAsLazyPagingItems()
    val dWebData by viewModel.dWebData.observeAsState(initial = null)
    val sceneType by viewModel.sceneType.observeAsState(initial = BalancesSceneType.Token)
    val currentWallet by viewModel.currentWallet.observeAsState(initial = null)
    val wallets by viewModel.wallets.observeAsState(initial = emptyList())
    val displayChainType by viewModel.displayChainType.observeAsState(initial = null)
    if (currentWallet == null) {
        WalletIntroScene(
            onCreate = {
                rootNavController.navigate("WalletIntroHostLegal/${CreateType.CREATE}")
            },
            onImport = {
                rootNavController.navigate("WalletIntroHostLegal/${CreateType.IMPORT}")
            },
            onConnect = {
                rootNavController.navigate("SwitchWalletAddWalletConnect")
            }
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
                        rootNavController.navigate("WalletQrcode/${dWebData.chainType.name}")
                    },
                    onSendClicked = {
                        rootNavController.navigate("SendTokenScene/eth")
                    },
                    sceneType = sceneType,
                    onSceneTypeChanged = {
                        viewModel.setSceneType(it)
                    },
                    walletChainType = dWebData.chainType,
                    onCollectibleDetailClicked = {
                        rootNavController.navigate("CollectibleDetail/${it.id}")
                    },
                    onBack = onBack,
                    displayChainType = displayChainType,
                    onDisplayChainTypeClicked = {
                        viewModel.setCurrentDisplayChainType(it)
                    },
                    onWalletAddressClicked = {
                        clipboardManager.setText(buildAnnotatedString {
                            append(wallet.address)
                        })
                    },
                    collectible = collectible,
                )
            }
        }
    }
}
