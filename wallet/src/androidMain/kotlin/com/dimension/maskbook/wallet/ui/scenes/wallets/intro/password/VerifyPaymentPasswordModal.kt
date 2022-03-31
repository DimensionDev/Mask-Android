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
package com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.viewmodel.wallets.UnlockWalletViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun VerifyPaymentPasswordModal(
    onNext: () -> Unit,
) {
    val viewModel = getViewModel<UnlockWalletViewModel>()
    val password by viewModel.password.collectAsState()
    val canConfirm by viewModel.passwordValid.collectAsState(false)
    MaskModal(
        title = {
            Text(stringResource(R.string.scene_restore_titles_verify_wallet_password))
        }
    ) {
        Column {
            Text(text = stringResource(R.string.scene_setting_general_setup_payment_password))
            Spacer(modifier = Modifier.height(8.dp))
            MaskPasswordInputField(
                value = password,
                onValueChange = {
                    viewModel.setPassword(it)
                },
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onNext.invoke()
                },
                enabled = canConfirm
            ) {
                Text(text = stringResource(R.string.common_controls_next))
            }
        }
    }
}
