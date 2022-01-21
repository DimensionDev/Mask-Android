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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
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
    Column {
        Text(text = stringResource(R.string.scene_change_password_new_password))
        Spacer(modifier = Modifier.height(8.dp))
        MaskPasswordInputField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            modifier = Modifier.fillMaxWidth(),
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
