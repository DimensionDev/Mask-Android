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

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ui.notification.StringResNotificationEvent.Companion.show
import com.dimension.maskbook.common.ui.theme.modalScrimColor
import com.dimension.maskbook.common.ui.widget.LocalInAppNotification
import com.dimension.maskbook.common.ui.widget.rememberMaskBottomSheetNavigator
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.humanizeDollar
import com.dimension.maskbook.wallet.ext.humanizeToken
import com.dimension.maskbook.wallet.repository.UnlockType
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.ScanQrcodeScene
import com.dimension.maskbook.wallet.viewmodel.wallets.BiometricViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.AddContactViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.GasFeeViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SearchAddressViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SendConfirmViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SendTokenDataViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SendTokenViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalTime::class)
@Composable
fun SendTokenHost(
    tokenAddress: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    val context = LocalContext.current

    val bottomSheetNavigator = rememberMaskBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)

    val gasFeeViewModel = getViewModel<GasFeeViewModel> {
        parametersOf(21000.0)
    }
    val tokenDataViewModel = getViewModel<SendTokenDataViewModel> {
        parametersOf(tokenAddress)
    }
    val searchAddressViewModel = getViewModel<SearchAddressViewModel>()

    val gasLimit by gasFeeViewModel.gasLimit.observeAsState(initial = -1.0)
    val maxPriorityFee by gasFeeViewModel.maxPriorityFee.observeAsState(initial = -1.0)
    val maxFee by gasFeeViewModel.maxFee.observeAsState(initial = -1.0)
    val arrives by gasFeeViewModel.arrives.observeAsState(initial = "")
    val nativeToken by gasFeeViewModel.nativeToken.observeAsState(initial = null)
    val usdValue by gasFeeViewModel.usdValue.observeAsState(initial = BigDecimal.ZERO)
    val gasTotal by gasFeeViewModel.gasTotal.observeAsState(initial = BigDecimal.ZERO)
    val tokenData by tokenDataViewModel.tokenData.collectAsState()

    ModalBottomSheetLayout(
        bottomSheetNavigator,
        sheetBackgroundColor = MaterialTheme.colors.background,
        scrimColor = MaterialTheme.colors.modalScrimColor,
    ) {
        NavHost(
            navController = navController,
            startDestination = "SearchAddress",
            route = "SendTokenScene",
        ) {
            composable("SearchAddress") {
                val input by searchAddressViewModel.input.observeAsState()
                val contacts by searchAddressViewModel.contacts.observeAsState()
                val recent by searchAddressViewModel.recent.observeAsState()
                val ensData by searchAddressViewModel.ensData.observeAsState()
                val selectEnsData by searchAddressViewModel.selectEnsData.observeAsState()
                val canConfirm by searchAddressViewModel.canConfirm.observeAsState()
                val noTokenFound by tokenDataViewModel.noTokenFound.observeAsState(true)

                val clipboardManager = LocalClipboardManager.current
                val inAppNotification = LocalInAppNotification.current

                SearchAddressScene(
                    onBack = onBack,
                    tokenAddress = tokenAddress.ifEmpty { nativeToken?.symbol.orEmpty() },
                    query = input,
                    canConfirm = canConfirm,
                    ensData = ensData,
                    selectEnsData = selectEnsData,
                    onEnsDataClick = { ens ->
                        searchAddressViewModel.onSelectEns(ens)
                    },
                    onQueryChanged = {
                        searchAddressViewModel.onInputChanged(it)
                    },
                    contacts = contacts,
                    recent = recent,
                    noTokenFound = noTokenFound,
                    onBuyToken = { /*TODO Logic: buy token*/ },
                    onScanQrCode = {
                        navController.navigate("ScanQrCode")
                    },
                    onSearch = {
                        // nothing to do
                    },
                    onCopy = { address ->
                        clipboardManager.setText(buildAnnotatedString { append(address) })
                        inAppNotification.show(R.string.common_alert_copied_to_clipboard_title)
                    },
                    onClear = {
                        searchAddressViewModel.onInputChanged("")
                    },
                    onItemSelect = {
                        val address = it.address
                        searchAddressViewModel.addSendHistory(it.address, "")
                        navController.navigate("Send/$address")
                    },
                    onNext = {
                        val address = selectEnsData?.address ?: input
                        val name = selectEnsData?.name ?: ""
                        searchAddressViewModel.addSendHistory(address, name)
                        navController.navigate("Send/$address")
                    }
                )
            }
            composable("ScanQrCode") {
                ScanQrcodeScene(
                    onBack = { navController.popBackStack() },
                    onResult = {
                        Log.d("SendTokenHost", "onResult: $it")
                        navController.popBackStack()
                        searchAddressViewModel.onInputChanged(it)
                    }
                )
            }
            composable("SearchToken") {
                val walletTokens by tokenDataViewModel.walletTokens.observeAsState(emptyList())
                var query by remember { mutableStateOf("") }
                SearchTokenScene(
                    onBack = {
                        navController.popBackStack()
                    },
                    query = query,
                    onQueryChanged = {
                        query = it
                    },
                    onSelect = {
                        tokenDataViewModel.setTokenData(it.tokenData)
                        navController.popBackStack()
                    },
                    tokens = walletTokens
                )
            }
            composable(
                "Send/{address}",
                arguments = listOf(
                    navArgument("address") { type = NavType.StringType },
                ),
            ) {
                val address = it.arguments?.getString("address") ?: return@composable

                val viewModel = getViewModel<SendTokenViewModel> {
                    parametersOf(address)
                }
                val addressData by viewModel.addressData.observeAsState()
                val amount by viewModel.amount.observeAsState()
                val password by viewModel.password.observeAsState()
                val canConfirm by viewModel.canConfirm.observeAsState()

                val biometricViewModel = getViewModel<BiometricViewModel>()
                val biometricEnabled by biometricViewModel.biometricEnabled.observeAsState()

                val walletTokenData by tokenDataViewModel.walletTokenData.observeAsState()

                val currentTokenData = tokenData
                val currentAddressData = addressData
                val currentWalletTokenData = walletTokenData
                if (currentTokenData != null && currentAddressData != null && currentWalletTokenData != null) {
                    SendTokenScene(
                        onBack = { navController.popBackStack() },
                        addressData = currentAddressData,
                        onAddContact = { navController.navigate("AddContactSheet/$address") },
                        tokenData = currentTokenData,
                        walletTokenData = currentWalletTokenData,
                        onSelectToken = { navController.navigate("SearchToken") },
                        amount = amount,
                        maxAmount = currentWalletTokenData.count,
                        onAmountChanged = { viewModel.setAmount(it) },
                        unlockType = if (biometricEnabled) UnlockType.BIOMETRIC else UnlockType.PASSWORD,
                        gasFee = (gasTotal * usdValue).humanizeDollar(),
                        arrivesIn = arrives,
                        onEditGasFee = { navController.navigate("EditGasFee") },
                        onSend = { type ->
                            if (type == UnlockType.BIOMETRIC) {
                                biometricViewModel.authenticate(
                                    context,
                                    onSuccess = {
                                        navController.navigate("SendConfirm/$address/$amount")
                                    }
                                )
                            } else {
                                navController.navigate("SendConfirm/$address/$amount")
                            }
                        },
                        sendError = "",
                        paymentPassword = password,
                        onPaymentPasswordChanged = { viewModel.setPassword(it) },
                        canConfirm = canConfirm,
                    )
                }
            }
            bottomSheet("EditGasFee") {
                val mode by gasFeeViewModel.gasPriceEditMode.collectAsState()
                EditGasPriceSheet(
                    price = (gasTotal * usdValue).humanizeDollar(),
                    costFee = gasTotal.humanizeToken(),
                    costFeeUnit = nativeToken?.symbol ?: stringResource(R.string.chain_short_name_eth),
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
            bottomSheet(
                "AddContactSheet/{address}",
                arguments = listOf(
                    navArgument("address") { type = NavType.StringType },
                ),
            ) {
                val address = it.arguments?.getString("address") ?: return@bottomSheet

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
            bottomSheet(
                "SendConfirm/{address}/{amount}",
                arguments = listOf(
                    navArgument("address") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.StringType },
                ),
            ) {
                val address = it.arguments?.getString("address") ?: return@bottomSheet
                val amountString = it.arguments?.getString("amount") ?: return@bottomSheet
                val amount = remember(amountString) { BigDecimal(amountString) }

                val viewModel = getViewModel<SendConfirmViewModel> {
                    parametersOf(address)
                }
                val deeplink by viewModel.deepLink.observeAsState(initial = "")
                val addressData by viewModel.addressData.observeAsState(initial = null)

                val currentTokenData = tokenData
                val currentAddressData = addressData
                if (currentTokenData != null && currentAddressData != null) {
                    SendConfirmSheet(
                        addressData = currentAddressData,
                        tokenData = currentTokenData,
                        sendPrice = amount.humanizeToken(),
                        gasFee = (gasTotal * usdValue).humanizeDollar(),
                        total = (amount * currentTokenData.price + gasTotal * usdValue).humanizeDollar(),
                        onConfirm = {
                            viewModel.send(currentTokenData, amount, gasLimit, maxFee, maxPriorityFee)
                            onDone.invoke()
                            // open Wallet App if it is connected
                            if (deeplink.isNotEmpty()) {
                                try {
                                    context.startActivity(
                                        Intent().apply {
                                            data = Uri.parse(deeplink)
                                        }
                                    )
                                } catch (e: Throwable) {
                                    // ignore
                                }
                            }
                        },
                        onCancel = { navController.popBackStack() },
                        onEditGasFee = { navController.navigate("EditGasFee") },
                    )
                }
            }
        }
    }
}
