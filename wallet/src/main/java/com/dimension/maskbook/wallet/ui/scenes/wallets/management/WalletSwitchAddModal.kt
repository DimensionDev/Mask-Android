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
package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.MaskButton
import com.dimension.maskbook.wallet.ui.widget.MaskListItem
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletSwitchAddModal(
    onCreate: () -> Unit,
    onImport: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            WalletSwitchAddItem(
                onClick = onCreate,
                icon = R.drawable.ic_create_wallet,
                text = {
                    Text(text = stringResource(R.string.scene_wallets_list_add_wallets_items_add))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            WalletSwitchAddItem(
                onClick = onImport,
                icon = R.drawable.ic_import_wallet,
                text = {
                    Text(text = stringResource(R.string.common_controls_import_wallet))
                }
            )
        }
    }
}

@Composable
private fun WalletSwitchAddItem(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    text: @Composable (() -> Unit),
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
        )
    }
}
