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
package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ui.widget.MaskBackButton
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.Dialog
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletKeystoreViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ImportWalletKeyStoreScene(
    wallet: String,
    onDone: (WalletCreateOrImportResult) -> Unit,
    onBack: () -> Unit,
) {
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = stringResource(R.string.scene_wallet_import_item_keystore))
                    }
                )
            }
        ) {
            val viewModel = getViewModel<ImportWalletKeystoreViewModel> {
                parametersOf(wallet)
            }
            val keystore by viewModel.keystore.observeAsState(initial = "")
            val password by viewModel.password.observeAsState(initial = "")
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
                    MaskInputField(
                        value = keystore,
                        onValueChange = { viewModel.setKeystore(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(124.dp),
                        placeholder = {
                            Text(text = stringResource(R.string.scene_wallet_keystore_keystore_placeholder))
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.scene_wallet_delete_password), style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.height(8.dp))
                    MaskPasswordInputField(
                        value = password,
                        onValueChange = { viewModel.setPassword(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = "original password")
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
                        Text(text = stringResource(R.string.common_controls_confirm))
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
