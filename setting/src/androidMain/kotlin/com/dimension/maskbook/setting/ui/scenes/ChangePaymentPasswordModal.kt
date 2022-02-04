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
package com.dimension.maskbook.wallet.ui.scenes.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.dimension.maskbook.localization.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.navHostAnimationDurationMillis
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.settings.PaymentPasswordSettingsViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChangePaymentPasswordModal(
    onConfirm: () -> Unit,
) {
    val viewModel: PaymentPasswordSettingsViewModel = getViewModel()

    val isNext by viewModel.isNext.observeAsState(false)
    val password by viewModel.password.observeAsState(initial = "")
    val newPassword by viewModel.newPassword.observeAsState(initial = "")
    val newPasswordConfirm by viewModel.newPasswordConfirm.observeAsState(initial = "")
    val confirmPassword by viewModel.confirmPassword.observeAsState(false)
    val confirmNewPassword by viewModel.confirmNewPassword.observeAsState(false)

    MaskModal(
        title = {
            Text(stringResource(R.string.scene_setting_general_change_payment_password))
        }
    ) {
        AnimatedContent(
            targetState = isNext,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(navHostAnimationDurationMillis)
                ) with slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(navHostAnimationDurationMillis)
                )
            },
            modifier = Modifier.padding(ScaffoldPadding),
        ) { next ->
            if (!next) {
                CheckPaymentPassword(
                    password = password,
                    onPasswordChange = { viewModel.setPassword(it) },
                    confirmPassword = confirmPassword,
                    onNext = { viewModel.goToNext() }
                )
            } else {
                ChangePaymentPassword(
                    newPassword = newPassword,
                    onNewPasswordChange = { viewModel.setNewPassword(it) },
                    newPasswordConfirm = newPasswordConfirm,
                    onNewPasswordConfirmChange = { viewModel.setNewPasswordConfirm(it) },
                    confirmNewPassword = confirmNewPassword,
                    onConfirm = {
                        viewModel.confirm()
                        onConfirm()
                    }
                )
            }
        }
    }
}

@Composable
private fun CheckPaymentPassword(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: Boolean,
    onNext: () -> Unit,
) {
    Column {
        Text(text = "Please verify current Payment Password")
        Spacer(Modifier.height(20.dp))
        Text(text = stringResource(R.string.scene_setting_general_setup_payment_password))
        Spacer(Modifier.height(8.dp))
        MaskPasswordInputField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = confirmPassword,
            onClick = onNext,
        ) {
            Text(text = stringResource(R.string.common_controls_next))
        }
    }
}

@Composable
private fun ChangePaymentPassword(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    newPasswordConfirm: String,
    onNewPasswordConfirmChange: (String) -> Unit,
    confirmNewPassword: Boolean,
    onConfirm: () -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(text = stringResource(R.string.scene_change_password_new_password))
        Spacer(modifier = Modifier.height(8.dp))
        MaskPasswordInputField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Next,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = stringResource(R.string.scene_set_password_confirm_payment_password))
        Spacer(modifier = Modifier.height(8.dp))
        MaskPasswordInputField(
            value = newPasswordConfirm,
            onValueChange = onNewPasswordConfirmChange,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.scene_change_password_password_demand),
            color = MaterialTheme.colors.primary,
        )
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = confirmNewPassword,
            onClick = onConfirm,
        ) {
            Text(text = stringResource(R.string.common_controls_confirm))
        }
    }
}
