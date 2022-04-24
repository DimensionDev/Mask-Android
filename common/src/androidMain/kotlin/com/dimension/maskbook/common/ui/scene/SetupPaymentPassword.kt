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
package com.dimension.maskbook.common.ui.scene

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.R
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.viewmodel.SetUpPaymentPasswordViewModel
import moe.tlaster.koin.compose.getViewModel

@Composable
fun SetUpPaymentPassword(
    onNext: () -> Unit,
) {
    val viewModel: SetUpPaymentPasswordViewModel = getViewModel()
    val newPassword by viewModel.newPassword.observeAsState(initial = "")
    val newPasswordConfirm by viewModel.newPasswordConfirm.observeAsState(initial = "")
    val canConfirm by viewModel.canConfirm.observeAsState(initial = false)
    MaskModal(
        title = {
            Text(
                text = stringResource(R.string.scene_set_password_title),
            )
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = stringResource(R.string.scene_setting_general_setup_payment_password))
            Spacer(modifier = Modifier.height(8.dp))
            MaskPasswordInputField(
                value = newPassword,
                onValueChange = {
                    viewModel.setNewPassword(it)
                },
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = stringResource(R.string.scene_set_password_repeat_payment_password))
            Spacer(modifier = Modifier.height(8.dp))
            MaskPasswordInputField(
                value = newPasswordConfirm,
                onValueChange = {
                    viewModel.setNewPasswordConfirm(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.scene_change_password_password_demand),
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.confirm()
                    onNext.invoke()
                },
                enabled = canConfirm
            ) {
                Text(text = stringResource(R.string.common_controls_next))
            }
        }
    }
}
