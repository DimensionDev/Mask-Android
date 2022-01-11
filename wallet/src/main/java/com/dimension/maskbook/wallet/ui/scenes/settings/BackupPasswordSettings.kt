package com.dimension.maskbook.wallet.ui.scenes.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton
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
                    text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_set_backup_password_title),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_backup_recovery_change_backup_password),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (currentPassword.isNotEmpty()) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_backup_recovery_back_up_password))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        viewModel.setPassword(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_change_backup_password_new_backup_password))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    viewModel.setNewPassword(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Confirm New Backup Password")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newPasswordConfirm,
                onValueChange = {
                    viewModel.setNewPasswordConfirm(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Backup password must be between 8 and 20 \n" +
                        "characters and contains at least a number, \n" +
                        "a uppercase letter, a lowercase letter and a \n" +
                        "special character.",
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
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_cancel))
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
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
                }
            }
        }
    }
}