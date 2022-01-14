package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.Dialog
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletMnemonicViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ImportWalletMnemonicScene(
    onBack: () -> Unit,
    wallet: String,
    onDone: (code: String) -> Unit
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
            Box {
                val viewModel = getViewModel<ImportWalletMnemonicViewModel> {
                    parametersOf(wallet)
                }
                val words by viewModel.words.observeAsState(initial = "")
                val canConfirm by viewModel.canConfirm.observeAsState(initial = false)
                val hintWords by viewModel.hintWords.observeAsState(initial = emptyList())
                var showDialog by remember {
                    mutableStateOf(false)
                }
                var result by remember {
                    mutableStateOf<WalletCreateOrImportResult?>(null)
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ScaffoldPadding),
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_mnemonic_title), style = MaterialTheme.typography.h4)
                    Spacer(modifier = Modifier.height(24.dp))
                    MaskInputField(
                        value = words,
                        onValueChange = { viewModel.setWords(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(124.dp),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.scene_identity_mnemonic_import_placeholder)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.confirm {
                                if (it.type == WalletCreateOrImportResult.Type.SUCCESS) {
                                    onDone.invoke(words)
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
                    AnimatedVisibility(visible = hintWords.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(top = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(hintWords) { word ->
                                TextButton(
                                    onClick = { viewModel.applyHintWords(word) },
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colors.surface,
                                            shape = MaterialTheme.shapes.small,
                                        )
                                ) {
                                    Text(text = word)
                                }
                            }
                        }
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