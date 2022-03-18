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
package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.applyTextStyle
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MiddleEllipsisText
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.WalletData

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletManagementModal(
    walletData: WalletData?,
    onRename: () -> Unit,
    onBackup: () -> Unit,
    onTransactionHistory: () -> Unit,
    onDelete: () -> Unit,
    onDisconnect: () -> Unit,
) {
    MaskModal(
        title = {
            MiddleEllipsisText(
                text = walletData?.address.orEmpty(),
                modifier = Modifier.fillMaxWidth(0.5f)
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            WalletManagementItem(
                onClick = onRename,
                icon = R.drawable.ic_rename_wallet,
                text = {
                    Text(text = stringResource(R.string.scene_wallet_edit_item_rename))
                },
                trailing = {
                    Text(text = walletData?.name.orEmpty())
                }
            )
            if (walletData != null && !walletData.fromWalletConnect) {
                WalletManagementItem(
                    onClick = onBackup,
                    icon = R.drawable.ic_back_up,
                    text = {
                        Text(text = stringResource(R.string.scene_personas_action_backup))
                    },
                )
            }
            WalletManagementItem(
                onClick = onTransactionHistory,
                icon = R.drawable.ic_transaction_history,
                text = {
                    Text(text = stringResource(R.string.scene_wallet_detail_wallet_items_history))
                },
            )
            if (walletData != null && walletData.fromWalletConnect) {
                WalletManagementItem(
                    onClick = onDisconnect,
                    icon = R.drawable.ic_disconnect,
                    text = {
                        Text(
                            text = stringResource(R.string.scene_wallet_connect_disconnect),
                            color = Color.Red,
                        )
                    },
                )
            } else {
                WalletManagementItem(
                    onClick = onDelete,
                    icon = R.drawable.ic_delete_wallet,
                    text = {
                        Text(
                            text = stringResource(R.string.scene_wallet_edit_item_delete),
                            color = Color.Red,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun WalletManagementItem(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    text: @Composable (() -> Unit),
    trailing: @Composable (() -> Unit)? = null,
) {
    MaskButton(onClick = onClick) {
        MaskListItem(
            icon = {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            },
            text = text,
            trailing = applyTextStyle(MaterialTheme.typography.h5, trailing),
        )
    }
}
