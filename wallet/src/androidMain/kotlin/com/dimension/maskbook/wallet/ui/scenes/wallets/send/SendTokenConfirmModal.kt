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
package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.fromHexString
import com.dimension.maskbook.common.ext.humanizeDollar
import com.dimension.maskbook.common.ext.humanizeToken
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.composable
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.routeProcessor.annotations.Query
import com.dimension.maskbook.wallet.export.model.SendTransactionData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.model.SendTokenRequest
import com.dimension.maskbook.wallet.repository.GasPriceEditMode
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.ui.scenes.wallets.UnlockWalletDialog
import com.dimension.maskbook.wallet.viewmodel.wallets.UnlockWalletViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.GasFeeViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.Web3TransactionConfirmViewModel
import moe.tlaster.koin.compose.getViewModel
import moe.tlaster.precompose.navigation.NavController
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavController
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal

@NavGraphDestination(
    route = WalletRoute.SendTokenConfirm.path,
    deeplink = [
        Deeplinks.Wallet.SendTokenConfirm.path,
    ],
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun SendTokenConfirmModal(
    rootNavController: NavController,
    @Back onBack: () -> Unit,
    @Path("dataRaw") dataRaw: String,
    @Query("requestRaw") requestRaw: String?,
) {
    val data = remember(dataRaw) { dataRaw.decodeJson<SendTransactionData>() }
    val request = remember(requestRaw) { requestRaw?.decodeJson<SendTokenRequest>() }

    val navController = rememberNavController()
    val viewModel = getViewModel<Web3TransactionConfirmViewModel> {
        parametersOf(data, request)
    }
    val token by viewModel.tokenData.collectAsState(null)
    val address by viewModel.addressData.collectAsState(null)
    val amount by viewModel.amount.collectAsState(BigDecimal.ZERO)
    address?.let { addressData ->
        token?.let { tokenData ->
            val gasFeeViewModel = getViewModel<GasFeeViewModel> {
                parametersOf(data.gasLimit?.fromHexString()?.toDouble() ?: 21000.0)
            }
            val gasLimit by gasFeeViewModel.gasLimit.collectAsState()
            val maxPriorityFee by gasFeeViewModel.maxPriorityFeePerGas.collectAsState()
            val maxFee by gasFeeViewModel.maxFeePerGas.collectAsState()
            val arrives by gasFeeViewModel.arrives.collectAsState()
            val gasUsdTotal by gasFeeViewModel.gasUsdTotal.collectAsState()
            val gasTotal by gasFeeViewModel.gasTotal.collectAsState()
            val loadingState by gasFeeViewModel.loadingState.collectAsState()
            val gasFeeUnit by gasFeeViewModel.gasFeeUnit.collectAsState()

            NavHost(
                navController,
                initialRoute = "SendConfirm"
            ) {
                composable("SendConfirm") {
                    val sending by viewModel.loadingState.collectAsState()
                    SendConfirmSheet(
                        addressData = addressData,
                        tokenData = WalletTokenData(
                            count = BigDecimal.ZERO,
                            tokenAddress = tokenData.address,
                            tokenData = tokenData
                        ),
                        sendPrice = amount.humanizeToken(),
                        gasFee = gasTotal.humanizeToken() + " " + gasFeeUnit,
                        total = (amount * tokenData.price + gasUsdTotal).humanizeDollar(),
                        sending = sending,
                        confirmEnabled = !loadingState,
                        onConfirm = {
                            navController.navigate("UnlockWalletDialog")
                        },
                        onCancel = {
                            viewModel.cancel()
                            onBack.invoke()
                        },
                        onEditGasFee = {
                            navController.navigate("EditGasFee")
                            gasFeeViewModel.refreshSuggestGasFee()
                        },
                    )
                }
                composable("EditGasFee") {
                    val mode by gasFeeViewModel.gasPriceEditMode.collectAsState(initial = GasPriceEditMode.MEDIUM)
                    val loading by gasFeeViewModel.loadingState.collectAsState()
                    EditGasPriceSheet(
                        price = gasUsdTotal.humanizeDollar(),
                        costFee = gasTotal.humanizeToken(),
                        costFeeUnit = tokenData.symbol,
                        arrivesIn = arrives,
                        mode = mode,
                        loading = loading,
                        gasLimit = gasLimit.toString(),
                        onGasLimitChanged = {
                            gasFeeViewModel.setGasLimit(
                                it.toDoubleOrNull() ?: 0.0
                            )
                        },
                        maxPriorityFee = maxPriorityFee.toString(),
                        maxPriorityFeePrice = "",
                        onMaxPriorityFeeChanged = {
                            gasFeeViewModel.setMaxPriorityFee(
                                it.toDoubleOrNull() ?: 0.0
                            )
                        },
                        maxFee = maxFee.toString(),
                        maxFeePrice = "",
                        onMaxFeeChanged = {
                            gasFeeViewModel.setMaxFee(
                                it.toDoubleOrNull() ?: 0.0
                            )
                        },
                        onSelectMode = { gasFeeViewModel.setGasPriceEditMode(it) },
                        gasLimitError = null,
                        maxPriorityFeeError = null,
                        maxFeeError = null,
                        canConfirm = gasLimit != -1.0 && maxFee != -1.0 && maxPriorityFee != -1.0,
                        onConfirm = {
                            navController.popBackStack()
                        }
                    )
                }
                dialog(
                    "UnlockWalletDialog",
                ) {
                    val unlockViewModel = getViewModel<UnlockWalletViewModel>()
                    val biometricEnable by unlockViewModel.biometricEnabled.collectAsState(initial = false)
                    val password by unlockViewModel.password.collectAsState(initial = "")
                    val passwordValid by unlockViewModel.passwordValid.collectAsState(initial = false)
                    val context = LocalContext.current
                    val onSuccess: () -> Unit = {
                        navController.popBackStack()
                        viewModel.send(
                            gasLimit = gasLimit,
                            maxFee = maxFee,
                            maxPriorityFee = maxPriorityFee,
                            onResult = { transactionHash ->
                                onBack.invoke()
                                rootNavController.sendEvent(ResultEvent.TokenConfirm(transactionHash))
                            }
                        )
                    }
                    UnlockWalletDialog(
                        onBack = { navController.popBackStack() },
                        biometricEnabled = biometricEnable,
                        password = password,
                        onPasswordChanged = { unlockViewModel.setPassword(it) },
                        passwordValid = passwordValid,
                        onConfirm = {
                            if (biometricEnable) {
                                unlockViewModel.authenticate(
                                    context = context,
                                    onSuccess = {
                                        onSuccess.invoke()
                                    }
                                )
                            } else {
                                if (passwordValid) {
                                    onSuccess.invoke()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
