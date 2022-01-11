package com.dimension.maskbook.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dimension.maskbook.wallet.ext.*
import com.dimension.maskbook.wallet.repository.*
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.AddContactSheet
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.EditGasPriceSheet
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.SendConfirmSheet
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.register.UserNameModalViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.AddContactViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.GasFeeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal

class ComposeBottomSheetDialogFragment(
    private val route: String = "",
    private val data: Any? = null,
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return context?.let {
            ComposeView(it).apply {
                setContent {
                    MaskTheme {
                        when (route) {
                            "UserNameInput" -> {
                                UserNameModal(
                                    onDone = {
                                        this@ComposeBottomSheetDialogFragment.dismiss()
                                    },
                                )
                            }
                            "SendTokenConfirm" -> {
                                if (data is SendTokenConfirmData) {
                                    SendTokenConfirmModal(
                                        data,
                                        onDone = {
                                            this@ComposeBottomSheetDialogFragment.dismiss()
                                        },
                                        onCancel = {
                                            this@ComposeBottomSheetDialogFragment.dismiss()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "ComposeBottomSheetDialogFragment"
    }
}

@Composable
private fun SendTokenConfirmModal(
    data: SendTokenConfirmData,
    onDone: () -> Unit,
    onCancel: () -> Unit,
) {
    val navController = rememberNavController()
    val repository = get<IWalletRepository>()
    val historyRepository = get<ISendHistoryRepository>()
    data.data.to?.let { address ->
        val addressData by historyRepository.getOrCreateByAddress(address)
            .observeAsState(initial = null)
        val wallet by repository.currentWallet.observeAsState(initial = null)
        wallet?.let { wallet ->
            addressData?.let { addressData ->
                wallet.tokens.firstOrNull { it.tokenData.address == "eth" }?.tokenData?.let { tokenData ->
                    val gasFeeViewModel = getViewModel<GasFeeViewModel> {
                        parametersOf(data.data.gas?.fromHexString()?.toDouble() ?: 21000.0)
                    }
                    val gasPrice by gasFeeViewModel.gasPrice.observeAsState(initial = BigDecimal.ZERO)
                    val gasLimit by gasFeeViewModel.gasLimit.observeAsState(initial = -1.0)
                    val maxPriorityFee by gasFeeViewModel.maxPriorityFee.observeAsState(initial = -1.0)
                    val maxFee by gasFeeViewModel.maxFee.observeAsState(initial = -1.0)
                    val arrives by gasFeeViewModel.arrives.observeAsState(initial = "")
                    val ethPrice by gasFeeViewModel.ethPrice.observeAsState(initial = BigDecimal.ZERO)
                    val amount = data.data.value?.hexWei?.ether ?: BigDecimal.ZERO
                    val gasTotal by gasFeeViewModel.gasTotal.observeAsState(initial = BigDecimal.ZERO)
                    val chainType = data.data.chainId?.let { chainId ->
                        ChainType.values().firstOrNull { it.chainId == chainId }
                    } ?: ChainType.eth
                    NavHost(
                        navController,
                        startDestination = "SendConfirm"
                    ) {
                        composable("SendConfirm") {
                            SendConfirmSheet(
                                addressData = addressData,
                                tokenData = tokenData,
                                sendPrice = amount.humanizeToken(),
                                gasFee = (gasTotal * ethPrice).humanizeDollar(),
                                total = (amount * tokenData.price + gasTotal * ethPrice).humanizeDollar(),
                                onConfirm = {
                                    repository.sendTokenWithCurrentWalletAndChainType(
                                        amount = amount,
                                        address = address,
                                        chainType = chainType,
                                        gasLimit = gasLimit,
                                        gasFee = gasPrice,
                                        maxFee = maxFee,
                                        maxPriorityFee = maxPriorityFee,
                                        data = data.data.data ?: "",
                                        onDone = {
                                            data.onDone.invoke(it)
                                        },
                                        onError = {
                                            data.onError.invoke(it)
                                        }
                                    )
                                    onDone.invoke()
                                },
                                onCancel = {
                                    data.onCancel.invoke()
                                    onCancel.invoke()
                                },
                                onEditGasFee = { navController.navigate("EditGasFee") },
                            )
                        }
                        composable("EditGasFee") {
                            val mode by gasFeeViewModel.gasPriceEditMode.observeAsState(initial = GasPriceEditMode.MEDIUM)
                            EditGasPriceSheet(
                                price = (gasTotal * ethPrice).humanizeDollar(),
                                costFee = gasTotal.humanizeToken(),
                                costFeeUnit = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.chain_short_name_eth), // TODO:
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
                    }
                }
            }
        }
    }
}

@Composable
private fun UserNameModal(
    onDone: () -> Unit,
) {
    val viewModel = getViewModel<UserNameModalViewModel>()
    val name by viewModel.userName.observeAsState(initial = "")
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            Text(text = "User Name")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { viewModel.setUserName(it) },
            )
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.done(name)
                    onDone.invoke()
                },
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
            }
        }
    }
}
