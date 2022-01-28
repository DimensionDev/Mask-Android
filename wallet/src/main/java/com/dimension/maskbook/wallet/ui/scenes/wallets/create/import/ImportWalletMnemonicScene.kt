/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.Dialog
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskInputField
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
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
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = stringResource(R.string.scene_wallet_mnemonic_title))
                    }
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
                        Text(text = stringResource(R.string.common_controls_confirm))
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
