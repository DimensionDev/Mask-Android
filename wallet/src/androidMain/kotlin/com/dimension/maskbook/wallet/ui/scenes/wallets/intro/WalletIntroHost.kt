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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.route.transfer
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.CreateType
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletBalancesScene
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletBalancesViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.getViewModel

@ExperimentalMaterialNavigationApi
@Composable
fun WalletIntroHost(navController: NavController) {
    val clipboardManager = LocalClipboardManager.current
    val viewModel = getViewModel<WalletBalancesViewModel>()
    val collectible = viewModel.collectible.collectAsLazyPagingItems()
    val dWebData by viewModel.dWebData.observeAsState()
    val sceneType by viewModel.sceneType.observeAsState()
    val wallet by viewModel.currentWallet.observeAsState()
    val wallets by viewModel.wallets.observeAsState()
    val displayChainType by viewModel.displayChainType.observeAsState()
    val showTokens by viewModel.showTokens.observeAsState()
    val showTokensLess by viewModel.showTokensLess.observeAsState()
    val showTokensLessAmount by viewModel.showTokensLessAmount.observeAsState()
    val connectedDApps by viewModel.connectedDApp.observeAsState()
    val nativeToken by viewModel.walletNativeToken.observeAsState()
    val currentWallet = wallet
    val currentDWebData = dWebData

    if (currentWallet == null) {
        WalletIntroScene(
            onCreate = {
                navController.navigate(WalletRoute.WalletIntroHostLegal(CreateType.CREATE.name))
            },
            onImport = {
                navController.navigate(WalletRoute.WalletIntroHostLegal(CreateType.IMPORT.name))
            },
            onConnect = {
                navController.navigate(WalletRoute.SwitchWalletAddWalletConnect)
            }
        )
    } else if (currentDWebData != null) {
        val swipeRefreshState = rememberSwipeRefreshState(false)
        val refreshing by viewModel.refreshingWallet.observeAsState()
        swipeRefreshState.isRefreshing = refreshing
        WalletBalancesScene(
            wallets = wallets,
            currentWallet = currentWallet,
            showTokens = showTokens,
            showTokensLess = showTokensLess,
            showTokensLessAmount = showTokensLessAmount,
            onWalletChanged = {
                viewModel.setCurrentWallet(it)
            },
            onWalletMenuClicked = {
                navController.navigate(WalletRoute.WalletBalancesMenu)
            },
            onWalletSwitchClicked = {
                navController.navigate(WalletRoute.SwitchWallet)
            },
            onTokenDetailClicked = {
                navController.navigate(WalletRoute.TokenDetail(it.address))
            },
            onReceiveClicked = {
                navController.navigate(WalletRoute.WalletQrcode(currentDWebData.chainType.name))
            },
            onSendClicked = {
                nativeToken?.let {
                    navController.transfer(walletNativeToken = it, tradableId = null)
                }
            },
            sceneType = sceneType,
            onSceneTypeChanged = {
                viewModel.setSceneType(it)
            },
            walletChainType = currentDWebData.chainType,
            onCollectibleDetailClicked = {
                navController.navigate(WalletRoute.CollectibleDetail(it.id))
            },
            displayChainType = displayChainType,
            onDisplayChainTypeClicked = {
                viewModel.setCurrentDisplayChainType(it)
            },
            onWalletAddressClicked = {
                clipboardManager.setText(
                    buildAnnotatedString {
                        append(currentWallet.address)
                    }
                )
            },
            collectible = collectible,
            refreshState = swipeRefreshState,
            onWalletRefresh = {
                viewModel.refreshWallet()
            },
            onScan = {
                // TODO Scan
            },
            connectedDAppCount = connectedDApps.size,
            onDisplayWalletConnect = {
            }
        )
    }
}
