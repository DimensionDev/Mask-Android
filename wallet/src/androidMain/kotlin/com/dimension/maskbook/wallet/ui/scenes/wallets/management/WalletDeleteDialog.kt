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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.NameImage
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.common.ui.widget.SecondaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.WalletData

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WalletDeleteDialog(
    walletData: WalletData,
    biometricEnabled: Boolean,
    password: String,
    onPasswordChanged: (String) -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    passwordValid: Boolean,
) {
    MaskDialog(
        // workaround for https://issuetracker.google.com/issues/194911971
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(horizontal = 23.dp),
        onDismissRequest = onBack,
        icon = {
            NameImage(
                name = walletData.name,
                modifier = Modifier.size(36.dp),
            )
        },
        title = {
            Text(text = stringResource(R.string.scene_wallet_edit_item_delete))
        },
        text = {
            Column {
                Text(text = walletData.address)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(R.string.scene_wallet_delete_content))
            }
        },
        buttons = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(visible = !biometricEnabled) {
                    Text(text = stringResource(R.string.scene_setting_general_setup_payment_password))
                    MaskPasswordInputField(value = password, onValueChange = onPasswordChanged)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = stringResource(R.string.common_controls_cancel))
                    }
                    PrimaryButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        enabled = passwordValid || biometricEnabled
                    ) {
                        Text(text = stringResource(R.string.scene_personas_action_delete))
                    }
                }
            }
        }
    )
}
