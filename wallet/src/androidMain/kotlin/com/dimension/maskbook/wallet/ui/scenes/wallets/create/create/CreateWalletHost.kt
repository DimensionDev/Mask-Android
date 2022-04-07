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
package com.dimension.maskbook.wallet.ui.scenes.wallets.create.create

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.dimension.maskbook.common.ext.getNestedNavigationViewModel
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.viewmodel.wallets.create.CreateWalletRecoveryKeyViewModel
import org.koin.core.parameter.parametersOf

private const val GeneratedRouteName = "createWalletRoute"

@NavGraphDestination(
    route = WalletRoute.CreateWallet.Pharse.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun PharseRoute(
    navController: NavController,
    @Path("wallet") wallet: String,
    @Back onBack: () -> Unit,
) {
    val viewModel: CreateWalletRecoveryKeyViewModel = navController
        .getNestedNavigationViewModel(WalletRoute.CreateWallet.Route) {
            parametersOf(wallet)
        }
    val words by viewModel.words.observeAsState(initial = emptyList())
    MnemonicPhraseScene(
        words = words.map { it.word },
        onRefreshWords = {
            viewModel.refreshWords()
        },
        onVerify = { navController.navigate(WalletRoute.CreateWallet.Verify(wallet)) },
        onBack = onBack,
    )
}

@NavGraphDestination(
    route = WalletRoute.CreateWallet.Confirm,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ConfirmRoute(
    navController: NavController,
) {
    val onDone = remember {
        {
            navController.navigate(
                Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Wallet)),
                navOptions = navOptions {
                    launchSingleTop = true
                    popUpTo(CommonRoute.Main.Home.path) {
                        inclusive = false
                    }
                }
            )
        }
    }
    MaskDialog(
        onDismissRequest = {
            onDone.invoke()
        },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_success),
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.common_alert_wallet_create_success_title))
        },
        text = {
            Text(text = stringResource(R.string.common_alert_wallet_create_success_description))
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDone.invoke()
                },
            ) {
                Text(text = stringResource(R.string.common_controls_done))
            }
        },
    )
}
