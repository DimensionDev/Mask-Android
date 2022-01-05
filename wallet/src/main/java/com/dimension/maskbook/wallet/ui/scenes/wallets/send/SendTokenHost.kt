package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.plusAssign
import com.dimension.maskbook.wallet.ext.humanizeDollar
import com.dimension.maskbook.wallet.ext.humanizeToken
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.GasPriceEditMode
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.TokenData
import com.dimension.maskbook.wallet.repository.UnlockWays
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.viewmodel.wallets.send.*
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalTime::class)
@Composable
fun SendTokenHost(
    tokenData: TokenData,
    onDone: () -> Unit,
) {
    val rootNavController = LocalRootNavController.current
    val navController = rememberNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    val gasFeeViewModel = getViewModel<GasFeeViewModel> {
        parametersOf(21000.0)
    }
    val gasFee by gasFeeViewModel.gasPrice.observeAsState(initial = BigDecimal.ZERO)
    val gasLimit by gasFeeViewModel.gasLimit.observeAsState(initial = -1.0)
    val maxPriorityFee by gasFeeViewModel.maxPriorityFee.observeAsState(initial = -1.0)
    val maxFee by gasFeeViewModel.maxFee.observeAsState(initial = -1.0)
    val arrives by gasFeeViewModel.arrives.observeAsState(initial = "")
    val ethPrice by gasFeeViewModel.ethPrice.observeAsState(initial = BigDecimal.ZERO)
    val gasTotal by gasFeeViewModel.gasTotal.observeAsState(initial = BigDecimal.ZERO)

    ModalBottomSheetLayout(
        bottomSheetNavigator,
        sheetBackgroundColor = MaterialTheme.colors.background,
    ) {
        NavHost(
            navController = navController,
            startDestination = "SearchAddress",
            route = "SendTokenScene",
        ) {
            composable(
                "SearchAddress"
            ) {
                val viewModel = getViewModel<SearchAddressViewModel>()
                val input by viewModel.input.observeAsState(initial = "")
                val contacts by viewModel.contacts.observeAsState(initial = emptyList())
                val recent by viewModel.recent.observeAsState(initial = emptyList())
                val noTokenFound by viewModel.noTokenFound.observeAsState(initial = false)
//                val searchResult by viewModel.searchResult.observeAsState(initial = null)
//                val loading by viewModel.loading.observeAsState(initial = false)
                SearchAddressScene(
                    onBack = { rootNavController.popBackStack() },
                    tokenData = tokenData,
                    query = input,
                    onQueryChanged = { viewModel.onInputChanged(it) },
//                    searchResult = searchResult,
                    contacts = contacts,
                    recent = recent,
//                    showLoading = loading,
                    noTokenFound = noTokenFound,
                    onBuyToken = { /*TODO Logic: buy token*/ },
                    onScanQrCode = { /*TODO Logic: scan qr code*/ },
                    onNext = {
                        viewModel.addSendHistory(it)
                        navController.navigate("Send/${it}")
                    }
                )
            }
            composable(
                "Send/{address}",
                arguments = listOf(
                    navArgument("address") { type = NavType.StringType },
                ),
            ) {
                it.arguments?.getString("address")?.let { address ->
                    val viewModel = getViewModel<SendTokenViewModel> {
                        parametersOf(tokenData, address)
                    }
                    val addressData by viewModel.addressData.observeAsState(initial = null)
                    val walletTokenData by viewModel.walletTokenData.observeAsState(initial = null)
                    val amount by viewModel.amount.observeAsState(initial = "0")
                    val password by viewModel.password.observeAsState(initial = "")
                    val canConfirm by viewModel.canConfirm.observeAsState(initial = false)
                    val enableBiometric by get<ISettingsRepository>().biometricEnabled.observeAsState(
                        initial = false
                    )

                    addressData?.let { addressData ->
                        walletTokenData?.let { walletTokenData ->
                            SendTokenScene(
                                onBack = { navController.popBackStack() },
                                addressData = addressData,
                                onAddContact = { navController.navigate("AddContactSheet/${address}") },
                                tokenData = tokenData,
                                walletTokenData = walletTokenData,
                                onSelectToken = { /*TODO*/ },
                                amount = amount,
                                maxAmount = walletTokenData.count,
                                onAmountChanged = { viewModel.setAmount(it) },
                                unlockWays = if (enableBiometric) UnlockWays.FACE_ID else UnlockWays.PASSWORD,
                                gasFee = (gasTotal * ethPrice).humanizeDollar(),
                                arrivesIn = arrives,
                                onEditGasFee = { navController.navigate("EditGasFee") },
                                onSend = {
                                    navController.navigate("SendConfirm/${address}/${amount}")
                                },
                                sendError = "",
                                paymentPassword = password,
                                onPaymentPasswordChanged = { viewModel.setPassword(it) },
                                canConfirm = canConfirm,
                            )
                        }
                    }
                }
            }
            bottomSheet("EditGasFee") {
                val mode by gasFeeViewModel.gasPriceEditMode.observeAsState(initial = GasPriceEditMode.MEDIUM)
                EditGasPriceSheet(
                    price = (gasTotal * ethPrice).humanizeDollar(),
                    costFee = gasTotal.humanizeToken(),
                    costFeeUnit = "ETH", // TODO:
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
            bottomSheet(
                "SendConfirm/{address}/{amount}",
                arguments = listOf(
                    navArgument("address") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.StringType },
                ),
            ) {
                it.arguments?.getString("amount")?.let { BigDecimal(it) }?.let { amount ->
                    it.arguments?.getString("address")?.let { address ->
                        val viewModel = getViewModel<SendConfirmViewModel> {
                            parametersOf(tokenData, address)
                        }
                        val addressData by viewModel.addressData.observeAsState(initial = null)
                        addressData?.let { addressData ->
                            SendConfirmSheet(
                                addressData = addressData,
                                tokenData = tokenData,
                                sendPrice = amount.humanizeToken(),
                                gasFee = (gasTotal * ethPrice).humanizeDollar(),
                                total = (amount * tokenData.price + gasTotal * ethPrice).humanizeDollar(),
                                onConfirm = {
                                    viewModel.send(amount, gasLimit, gasFee, maxFee, maxPriorityFee)
                                    onDone.invoke()
                                },
                                onCancel = { navController.popBackStack() },
                                onEditGasFee = { navController.navigate("EditGasFee") },
                            )
                        }
                    }
                }
            }
        }
    }
}