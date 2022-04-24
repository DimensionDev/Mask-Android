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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.paging.compose.collectAsLazyPagingItems
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.getNestedNavigationViewModel
import com.dimension.maskbook.common.ext.humanizeDollar
import com.dimension.maskbook.common.ext.humanizeToken
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.routeProcessor.annotations.Query
import com.dimension.maskbook.common.ui.barcode.ScanQrcodeScene
import com.dimension.maskbook.common.ui.notification.StringResNotificationEvent.Companion.show
import com.dimension.maskbook.common.ui.widget.LocalInAppNotification
import com.dimension.maskbook.common.viewmodel.BiometricViewModel
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.repository.UnlockType
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.viewmodel.wallets.send.AddContactViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.GasFeeViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SearchAddressViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SearchTradableViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SendConfirmViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.TransferDetailViewModel
import moe.tlaster.precompose.navigation.NavController
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

private const val GeneratedRouteName = "transferRoute"

@NavGraphDestination(
    route = WalletRoute.Transfer.SearchAddress.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun SearchAddressRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Query("tradableId") tradableId: String? = null,
) {

    val searchAddressViewModel = navController
        .getNestedNavigationViewModel<SearchAddressViewModel>(WalletRoute.Transfer.Route)
    val input by searchAddressViewModel.input.observeAsState()
    val contacts by searchAddressViewModel.contacts.observeAsState()
    val recent by searchAddressViewModel.recent.observeAsState()
    val ensData by searchAddressViewModel.ensData.observeAsState()
    val selectEnsData by searchAddressViewModel.selectEnsData.observeAsState()
    val canConfirm by searchAddressViewModel.canConfirm.observeAsState()

    val clipboardManager = LocalClipboardManager.current
    val inAppNotification = LocalInAppNotification.current

    SearchAddressScene(
        onBack = onBack,
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
        onScanQrCode = {
            navController.navigate(WalletRoute.Transfer.ScanQrCode)
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
            navController.navigate(WalletRoute.Transfer.Send(address, tradableId))
        },
        onNext = {
            val address = selectEnsData?.address ?: input
            val name = selectEnsData?.name ?: ""
            searchAddressViewModel.addSendHistory(address, name)
            navController.navigate(WalletRoute.Transfer.Send(address, tradableId))
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Transfer.ScanQrCode,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ScanQrcodeRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val searchAddressViewModel = navController
        .getNestedNavigationViewModel<SearchAddressViewModel>(WalletRoute.Transfer.Route)
    ScanQrcodeScene(
        onBack = { onBack.invoke() },
        onResult = {
            onBack.invoke()
            searchAddressViewModel.onInputChanged(it)
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Transfer.Send.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun SendRoute(
    navController: NavController,
    @Path("address") address: String,
    @Back onBack: () -> Unit,
    @Query("tradableId") tradableId: String? = null,
) {
    val context = LocalContext.current
    val gasFeeViewModel = navController
        .getNestedNavigationViewModel<GasFeeViewModel>(WalletRoute.Transfer.Route) {
            parametersOf(21000.0)
        }
    val transferDetailViewModel = navController
        .getNestedNavigationViewModel<TransferDetailViewModel>(WalletRoute.Transfer.Route) {
            parametersOf(tradableId)
        }

    val arrives by gasFeeViewModel.arrives.observeAsState(initial = "")
    val gasUsdTotal by gasFeeViewModel.gasUsdTotal.observeAsState(initial = BigDecimal.ZERO)
    val gasTotal by gasFeeViewModel.gasTotal.observeAsState(initial = BigDecimal.ZERO)

    val selectTradable by transferDetailViewModel.selectedTradable.collectAsState(null)

    LaunchedEffect(address) {
        transferDetailViewModel.setAddress(address)
    }
    val biometricViewModel = getViewModel<BiometricViewModel>()
    val biometricEnabled by biometricViewModel.biometricEnabled.observeAsState()
    val addressData by transferDetailViewModel.addressData.observeAsState()
    val amount by transferDetailViewModel.amount.observeAsState()
    val password by transferDetailViewModel.password.observeAsState()
    val canConfirm by transferDetailViewModel.canConfirm.observeAsState()
    val balance by transferDetailViewModel.balance.observeAsState()
    val maxAmount by transferDetailViewModel.maxAmount.observeAsState()
    val isEnoughForGas by transferDetailViewModel.isEnoughForGas.observeAsState()
    transferDetailViewModel.setGasTotal(gasTotal = gasTotal)

    TransferDetailScene(
        onBack = onBack,
        addressData = addressData,
        onAddContact = { navController.navigate(WalletRoute.Transfer.AddContactSheet(address)) },
        data = selectTradable,
        balance = balance,
        onSelectToken = {
            when (selectTradable) {
                is WalletCollectibleData -> navController.navigate(
                    WalletRoute.Transfer.SearchCollectibles(
                        tradableId
                    )
                )
                else -> navController.navigate(WalletRoute.Transfer.SearchToken(tradableId))
            }
        },
        amount = amount,
        maxAmount = maxAmount.humanizeToken(),
        onAmountChanged = { transferDetailViewModel.setAmount(it) },
        unlockType = if (biometricEnabled) UnlockType.BIOMETRIC else UnlockType.PASSWORD,
        gasFee = gasUsdTotal.humanizeDollar(),
        isEnoughForGas = isEnoughForGas,
        arrivesIn = arrives,
        onEditGasFee = {
            navController.navigate(WalletRoute.Transfer.EditGasFee)
            gasFeeViewModel.refreshSuggestGasFee()
        },
        onSend = { type ->
            if (type == UnlockType.BIOMETRIC) {
                biometricViewModel.authenticate(
                    context,
                    onSuccess = {
                        navController.navigate(
                            WalletRoute.Transfer.SendConfirm(
                                address,
                                amount,
                                tradableId
                            )
                        )
                    }
                )
            } else {
                navController.navigate(
                    WalletRoute.Transfer.SendConfirm(
                        address,
                        amount,
                        tradableId
                    )
                )
            }
        },
        sendError = "",
        paymentPassword = password,
        onPaymentPasswordChanged = { transferDetailViewModel.setPassword(it) },
        canConfirm = canConfirm
    )
}

@NavGraphDestination(
    route = WalletRoute.Transfer.SearchToken.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun SearchTokenRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Query("tradableId") tradableId: String? = null,
) {
    val transferDetailViewModel = navController
        .getNestedNavigationViewModel<TransferDetailViewModel>(WalletRoute.Transfer.Route) {
            parametersOf(tradableId)
        }

    val viewModel = getViewModel<SearchTradableViewModel>()
    val walletTokens by viewModel.walletTokens.observeAsState(emptyList())
    val query by viewModel.query.observeAsState()
    SearchTokenScene(
        onBack = {
            onBack.invoke()
        },
        query = query,
        onQueryChanged = {
            viewModel.onQueryChanged(it)
        },
        onSelect = {
            transferDetailViewModel.onSelectTradable(it)
            onBack.invoke()
        },
        tokens = walletTokens
    )
}

@NavGraphDestination(
    route = WalletRoute.Transfer.SearchCollectibles.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun SearchCollectiblesRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Query("tradableId") tradableId: String? = null,
) {

    val transferDetailViewModel = navController
        .getNestedNavigationViewModel<TransferDetailViewModel>(WalletRoute.Transfer.Route) {
            parametersOf(tradableId)
        }
    val viewModel = getViewModel<SearchTradableViewModel>()
    val walletCollectibleCollections =
        viewModel.walletCollectibleCollections.collectAsLazyPagingItems()
    val query by viewModel.query.observeAsState()
    SearchCollectibleScene(
        onBack = {
            onBack.invoke()
        },
        query = query,
        onQueryChanged = {
            viewModel.onQueryChanged(it)
        },
        onSelect = {
            transferDetailViewModel.onSelectTradable(it)
            onBack.invoke()
        },
        collections = walletCollectibleCollections
    )
}

@NavGraphDestination(
    route = WalletRoute.Transfer.EditGasFee,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun EditGasFeeRoute(
    navController: NavController,
) {
    val gasFeeViewModel = navController
        .getNestedNavigationViewModel<GasFeeViewModel>(WalletRoute.Transfer.Route) {
            parametersOf(21000.0)
        }

    val gasLimit by gasFeeViewModel.gasLimit.observeAsState(initial = -1.0)
    val maxPriorityFee by gasFeeViewModel.maxPriorityFeePerGas.observeAsState(initial = -1.0)
    val maxFee by gasFeeViewModel.maxFeePerGas.observeAsState(initial = -1.0)
    val arrives by gasFeeViewModel.arrives.observeAsState(initial = "")
    val gasTotal by gasFeeViewModel.gasTotal.observeAsState(initial = BigDecimal.ZERO)
    val gasUsdTotal by gasFeeViewModel.gasUsdTotal.observeAsState(initial = BigDecimal.ZERO)
    val mode by gasFeeViewModel.gasPriceEditMode.collectAsState()
    val loading by gasFeeViewModel.loadingState.observeAsState()
    val gasFeeUnit by gasFeeViewModel.gasFeeUnit.observeAsState()
    EditGasPriceSheet(
        price = gasUsdTotal.humanizeDollar(),
        costFee = gasTotal.humanizeToken(),
        costFeeUnit = gasFeeUnit,
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

@NavGraphDestination(
    route = WalletRoute.Transfer.AddContactSheet.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun AddContactSheetRoute(
    @Path("address") address: String,
    @Back onBack: () -> Unit,
) {
    val viewModel = getViewModel<AddContactViewModel>()
    val name by viewModel.name.observeAsState(initial = "")
    AddContactSheet(
        avatarLabel = name,
        address = address,
        canConfirm = name.isNotEmpty(),
        nameInput = name,
        onNameChanged = { viewModel.setName(it) },
        onAddContact = {
            viewModel.confirm(name, address, onResult = {
                onBack.invoke()
            })
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Transfer.SendConfirm.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun SendConfirmRoute(
    navController: NavController,
    @Path("address") address: String,
    @Path("amount") amountString: String,
    @Query("tradableId") tradableId: String?,
) {
    val context = LocalContext.current
    val gasFeeViewModel = navController
        .getNestedNavigationViewModel<GasFeeViewModel>(WalletRoute.Transfer.Route) {
            parametersOf(21000.0)
        }
    val transferDetailViewModel = navController
        .getNestedNavigationViewModel<TransferDetailViewModel>(WalletRoute.Transfer.Route) {
            parametersOf(tradableId)
        }

    val gasLimit by gasFeeViewModel.gasLimit.observeAsState(initial = -1.0)
    val maxPriorityFee by gasFeeViewModel.maxPriorityFeePerGas.observeAsState(initial = -1.0)
    val maxFee by gasFeeViewModel.maxFeePerGas.observeAsState(initial = -1.0)
    val gasUsdTotal by gasFeeViewModel.gasUsdTotal.observeAsState(initial = BigDecimal.ZERO)

    val selectTradable by transferDetailViewModel.selectedTradable.collectAsState(null)
    val amount = remember(amountString) { BigDecimal(amountString) }

    val viewModel = getViewModel<SendConfirmViewModel> {
        parametersOf(address)
    }
    val deeplink by viewModel.deepLink.observeAsState(initial = "")
    val addressData by viewModel.addressData.observeAsState(initial = null)
    val loading by viewModel.loadingState.observeAsState()
    val totalPrice = selectTradable?.let {
        when (it) {
            is WalletTokenData -> (amount * it.tokenData.price + gasUsdTotal).humanizeDollar()
            else -> gasUsdTotal.humanizeDollar()
        }
    } ?: ""
    SendConfirmSheet(
        addressData = addressData,
        tokenData = selectTradable,
        sendPrice = amount.humanizeToken(),
        gasFee = gasUsdTotal.humanizeDollar(),
        total = totalPrice,
        sending = loading,
        onConfirm = {
            selectTradable?.let {
                viewModel.send(
                    it,
                    amount,
                    gasLimit,
                    maxFee,
                    maxPriorityFee,
                    onDone = {
                        navController.popBackStack(
                            WalletRoute.Transfer.SearchAddress.path,
                            inclusive = true
                        )
                    },
                    onFailed = {}
                )
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
            }
        },
        onCancel = {
            viewModel.cancel()
            navController.popBackStack()
        },
        onEditGasFee = { navController.navigate(WalletRoute.Transfer.EditGasFee) },
    )
}
