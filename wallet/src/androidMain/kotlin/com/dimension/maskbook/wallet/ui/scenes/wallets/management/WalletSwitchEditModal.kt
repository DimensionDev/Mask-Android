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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.dimension.maskbook.common.ui.widget.MaskButton
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.WalletData

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletSwitchEditModal(
    walletData: WalletData,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDisconnect: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            WalletSwitchItem(
                onClick = onRename,
                icon = R.drawable.ic_rename_wallet,
                text = {
                    Text(text = stringResource(R.string.scene_wallet_edit_item_rename))
                },
                trailing = {
                    Text(text = walletData.name)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (walletData.fromWalletConnect) {
                WalletSwitchItem(
                    onClick = onDisconnect,
                    icon = R.drawable.ic_disconnect,
                    text = {
                        Text(
                            text = stringResource(R.string.scene_wallet_connect_disconnect),
                            color = Color.Red,
                        )
                    }
                )
            } else {
                WalletSwitchItem(
                    onClick = onDelete,
                    icon = R.drawable.ic_delete_wallet,
                    text = {
                        Text(
                            text = stringResource(R.string.scene_wallet_edit_item_delete),
                            color = Color.Red,
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun WalletSwitchItem(
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
