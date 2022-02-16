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
package com.dimension.maskbook.wallet.ui.scenes.wallets.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.LocalRootNavController
import com.dimension.maskbook.common.ui.widget.MaskBackButton
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.route.WalletRoute

@Composable
fun CreateOrImportWalletScene(
    onBack: () -> Unit,
    type: CreateType
) {
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                )
            }
        ) {
            val rootNavController = LocalRootNavController.current
            var input by remember {
                mutableStateOf("")
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 23.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
            ) {
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(123.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_create_wallet_logo),
                        contentDescription = null,
                    )
                }
                MaskButton(onClick = {}) {
                    MaskListItem(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_multi_chain_logo),
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(text = stringResource(R.string.scene_create_wallet_multichain_wallet_title))
                        },
                        trailing = {
                            MaskIconButton(
                                onClick = {
                                    rootNavController.navigate(WalletRoute.MultiChainWalletDialog)
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_doubt),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.scene_create_wallet_wallet_name),
                    style = MaterialTheme.typography.subtitle2,
                )
                Spacer(modifier = Modifier.height(8.dp))
                MaskInputField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(R.string.scene_create_wallet_wallet_name_placeholder))
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        when (type) {
                            CreateType.CREATE -> rootNavController.navigate(WalletRoute.CreateWallet(input))
                            CreateType.IMPORT -> rootNavController.navigate(WalletRoute.ImportWallet(input))
                        }
                    },
                    enabled = input.isNotEmpty()
                ) {
                    Text(text = stringResource(R.string.common_controls_accept))
                }
                Spacer(Modifier.height(58.dp))
            }
        }
    }
}

@Composable
private fun CreateSuccessDialog(onDismissRequest: () -> Unit) {
    MaskDialog(
        onDismissRequest = { /*TODO*/ },
        title = { Text(text = "Wallet successfully created!") },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_snccess),
                contentDescription = null
            )
        },
        buttons = {
            PrimaryButton(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.common_controls_done))
            }
        }
    )
}

enum class CreateType {
    CREATE,
    IMPORT
}
