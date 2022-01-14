package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.Dialog
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletPrivateKeyViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ImportWalletPrivateKeyScene(
    onBack: () -> Unit,
    onDone: (WalletCreateOrImportResult) -> Unit,
    wallet: String,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                )
            }
        ) {
            val viewModel = getViewModel<ImportWalletPrivateKeyViewModel> {
                parametersOf(wallet)
            }
            val privateKey by viewModel.privateKey.observeAsState(initial = "")
            val canConfirm by viewModel.canConfirm.observeAsState(initial = false)
            var showDialog by remember {
                mutableStateOf(false)
            }
            var result by remember {
                mutableStateOf<WalletCreateOrImportResult?>(null)
            }
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ScaffoldPadding),
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_private_key_title), style = MaterialTheme.typography.h4)
                    Spacer(modifier = Modifier.height(24.dp))
                    MaskInputField(
                        value = privateKey,
                        onValueChange = { viewModel.setPrivateKey(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(124.dp),
                        placeholder = {
                            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_private_key_placeholder))
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.confirm {
                                if (it.type == WalletCreateOrImportResult.Type.SUCCESS) {
                                    onDone.invoke(it)
                                } else {
                                    result = it
                                    showDialog = true
                                }

                            }
                        },
                        enabled = canConfirm
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
                    }
                }
                if (showDialog) {
                    result?.let {
                        it.Dialog(onDismissRequest = {
                            showDialog = false
                            result = null
                        })
                    }
                }
            }
        }
    }
}