package com.dimension.maskbook.wallet.ui.scenes.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.settings.BackupPasswordSettingsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun BackupPasswordSettings(
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    val viewModel: BackupPasswordSettingsViewModel = getViewModel()
    val currentPassword by viewModel.currentPassword.observeAsState(initial = "")
    val password by viewModel.password.observeAsState(initial = "")
    val newPassword by viewModel.newPassword.observeAsState(initial = "")
    val newPasswordConfirm by viewModel.newPasswordConfirm.observeAsState(initial = "")
    val canConfirm by viewModel.canConfirm.observeAsState(initial = false)
    MaskModal {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(ScaffoldPadding)
                .animateContentSize(),
        ) {
            if (currentPassword.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.scene_set_backup_password_title),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = stringResource(R.string.scene_setting_backup_recovery_change_backup_password),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (currentPassword.isNotEmpty()) {
                Text(text = stringResource(R.string.scene_setting_backup_recovery_back_up_password))
                Spacer(modifier = Modifier.height(8.dp))
                MaskPasswordInputField(
                    value = password,
                    onValueChange = {
                        viewModel.setPassword(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Next
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            Text(text = stringResource(R.string.scene_change_backup_password_new_backup_password))
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
            Text(text = stringResource(R.string.scene_set_backup_password_confirm_backup_password))
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
                text = stringResource(R.string.scene_set_backup_password_tips),
                color = MaterialTheme.colors.primary
            )

            Spacer(modifier = Modifier.height(20.dp))
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onBack.invoke()
                    }
                ) {
                    Text(text = stringResource(R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.confirm()
                        onConfirm.invoke()
                    },
                    enabled = canConfirm
                ) {
                    Text(text = stringResource(R.string.common_controls_confirm))
                }
            }
        }
    }
}