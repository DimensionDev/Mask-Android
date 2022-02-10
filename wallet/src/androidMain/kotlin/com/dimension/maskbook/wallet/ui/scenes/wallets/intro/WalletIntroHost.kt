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
package com.dimension.maskbook.wallet.ui.scenes.wallets.intro

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.paging.compose.collectAsLazyPagingItems
import com.dimension.maskbook.common.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.route.WalletRoute
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
    val showTokens by viewModel.showTokens.observeAsState(initial = emptyList())
    if (currentWallet == null) {
        WalletIntroScene(
            onCreate = {
                rootNavController.navigate(WalletRoute.WalletIntroHostLegal(CreateType.CREATE.name))
            },
            onImport = {
                rootNavController.navigate(WalletRoute.WalletIntroHostLegal(CreateType.IMPORT.name))
            },
            onConnect = {
                rootNavController.navigate(WalletRoute.SwitchWalletAddWalletConnect)
            }
        )
    } else {
        dWebData?.let { dWebData ->
            currentWallet?.let { wallet ->
                WalletBalancesScene(
                    wallets = wallets,
                    currentWallet = wallet,
                    showTokens = showTokens,
                    onWalletChanged = {
                        viewModel.setCurrentWallet(it)
                    },
                    onWalletMenuClicked = {
                        rootNavController.navigate(WalletRoute.WalletBalancesMenu)
                    },
                    onWalletSwitchClicked = {
                        rootNavController.navigate(WalletRoute.SwitchWallet)
                    },
                    onTokenDetailClicked = {
                        rootNavController.navigate(WalletRoute.TokenDetail(it.address))
                    },
                    onReceiveClicked = {
                        rootNavController.navigate(WalletRoute.WalletQrcode(dWebData.chainType.name))
                    },
                    onSendClicked = {
                        rootNavController.navigate(WalletRoute.SendTokenScene("eth"))
                    },
                    sceneType = sceneType,
                    onSceneTypeChanged = {
                        viewModel.setSceneType(it)
                    },
                    walletChainType = dWebData.chainType,
                    onCollectibleDetailClicked = {
                        rootNavController.navigate(WalletRoute.CollectibleDetail(it.id))
                    },
                    onBack = onBack,
                    displayChainType = displayChainType,
                    onDisplayChainTypeClicked = {
                        viewModel.setCurrentDisplayChainType(it)
                    },
                    onWalletAddressClicked = {
                        clipboardManager.setText(
                            buildAnnotatedString {
                                append(wallet.address)
                            }
                        )
                    },
                    collectible = collectible,
                )
            }
        }
    }
}
