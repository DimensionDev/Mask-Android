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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dimension.maskbook.common.ext.decodeBase64
import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.ext.fromHexString
import com.dimension.maskbook.wallet.ext.humanizeDollar
import com.dimension.maskbook.wallet.ext.humanizeToken
import com.dimension.maskbook.wallet.repository.GasPriceEditMode
import com.dimension.maskbook.wallet.repository.SendTokenConfirmData
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.ui.scenes.wallets.UnlockWalletDialog
import com.dimension.maskbook.wallet.viewmodel.wallets.UnlockWalletViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.AddContactViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.GasFeeViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.Web3TransactionConfirmViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal

@NavGraphDestination(
    route = WalletRoute.SendTokenConfirm.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun SendTokenConfirmModal(
    @Path("data") dataBase64: String,
    @Back onBack: () -> Unit,
) {
    val data = remember(dataBase64) { dataBase64.decodeBase64().decodeJson<SendTokenConfirmData>() }
    val navController = rememberNavController()
    val viewModel = getViewModel<Web3TransactionConfirmViewModel> {
        parametersOf(data)
    }
    val token by viewModel.tokenData.observeAsState(null)
    val address by viewModel.addressData.observeAsState(null)
    val amount by viewModel.amount.observeAsState(BigDecimal.ZERO)
    address?.let { addressData ->
        token?.let { tokenData ->
            val gasFeeViewModel = getViewModel<GasFeeViewModel> {
                parametersOf(data.data.gas?.fromHexString()?.toDouble() ?: 21000.0)
            }
            val gasLimit by gasFeeViewModel.gasLimit.observeAsState(initial = -1.0)
            val maxPriorityFee by gasFeeViewModel.maxPriorityFee.observeAsState(initial = -1.0)
            val maxFee by gasFeeViewModel.maxFee.observeAsState(initial = -1.0)
            val arrives by gasFeeViewModel.arrives.observeAsState(initial = "")
            val usdValue by gasFeeViewModel.usdValue.observeAsState(initial = BigDecimal.ZERO)
            val gasTotal by gasFeeViewModel.gasTotal.observeAsState(initial = BigDecimal.ZERO)
            NavHost(
                navController,
                startDestination = "SendConfirm"
            ) {
                composable("SendConfirm") {
                    SendConfirmSheet(
                        addressData = addressData,
                        tokenData = WalletTokenData(
                            count = BigDecimal.ZERO,
                            tokenAddress = tokenData.address,
                            tokenData = tokenData
                        ),
                        sendPrice = amount.humanizeToken(),
                        gasFee = (gasTotal * usdValue).humanizeDollar(),
                        total = (amount * tokenData.price + gasTotal * usdValue).humanizeDollar(),
                        // TODO Mimao update
                        sending = false,
                        onConfirm = {
                            navController.navigate("UnlockWalletDialog")
                        },
                        onCancel = {
                            viewModel.cancel()
                            onBack.invoke()
                        },
                        onEditGasFee = { navController.navigate("EditGasFee") },
                    )
                }
                composable("EditGasFee") {
                    val mode by gasFeeViewModel.gasPriceEditMode.observeAsState(initial = GasPriceEditMode.MEDIUM)
                    EditGasPriceSheet(
                        price = (gasTotal * usdValue).humanizeDollar(),
                        costFee = gasTotal.humanizeToken(),
                        costFeeUnit = tokenData.symbol,
                        arrivesIn = arrives,
                        mode = mode,
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
                composable(
                    "AddContactSheet/{address}",
                    arguments = listOf(
                        navArgument("address") { type = NavType.StringType },
                    ),
                ) {
                    it.arguments?.getString("address")?.let { address ->
                        val viewModel = getViewModel<AddContactViewModel>()
                        val name by viewModel.name.observeAsState(initial = "")
                        AddContactSheet(
                            avatarLabel = name,
                            address = address,
                            canConfirm = name.isNotEmpty(),
                            nameInput = name,
                            onNameChanged = { viewModel.setName(it) },
                            onAddContact = {
                                viewModel.confirm(name, address)
                                navController.popBackStack()
                            }
                        )
                    }
                }
                dialog(
                    "UnlockWalletDialog",
                ) {
                    val unlockViewModel = getViewModel<UnlockWalletViewModel>()
                    val biometricEnable by unlockViewModel.biometricEnabled.observeAsState(initial = false)
                    val password by unlockViewModel.password.observeAsState(initial = "")
                    val passwordValid by unlockViewModel.passwordValid.observeAsState(initial = false)
                    val context = LocalContext.current
                    val onSuccess: () -> Unit = {
                        navController.popBackStack()
                        viewModel.send(
                            gasLimit = gasLimit,
                            maxFee = maxFee,
                            maxPriorityFee = maxPriorityFee
                        )
                        onBack.invoke()
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
                                    onSuccess = { onSuccess.invoke() }
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
