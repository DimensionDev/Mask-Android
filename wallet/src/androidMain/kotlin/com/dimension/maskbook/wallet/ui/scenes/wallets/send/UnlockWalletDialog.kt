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
package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.wallet.R

@Composable
fun UnlockWalletDialog(
    unlockError: String?,
    onCancel: () -> Unit,
    onUnlock: (String) -> Unit,
) {
    var password by remember {
        mutableStateOf("")
    }
    MaskDialog(
        onDismissRequest = { /*TODO*/ },
        title = {
            Text(text = stringResource(R.string.scene_wallet_unlock_title))
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.scene_setting_general_setup_payment_password))
                Spacer(modifier = Modifier.height(8.dp))
                MaskPasswordInputField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (!unlockError.isNullOrEmpty()) {
                    Text(text = unlockError, color = MaterialTheme.colors.error)
                }
            }
        },
        buttons = {
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCancel.invoke()
                    }
                ) {
                    Text(text = stringResource(R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onUnlock.invoke(password)
                    },
                ) {
                    Text(text = stringResource(R.string.scene_wallet_unlock_button))
                }
            }
        }
    )
}
