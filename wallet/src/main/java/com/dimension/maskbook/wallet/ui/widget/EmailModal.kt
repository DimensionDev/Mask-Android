package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmailInputModal(
    email: String,
    onEmailChange: (String) -> Unit,
    emailValid: Boolean,
    onConfirm: () -> Unit,
    buttonEnabled: Boolean,
    title: String,
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6,
            )
            Spacer(modifier = Modifier.height(21.dp))
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_backup_backup_verify_field_email))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {
                    onEmailChange(it)
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
            if (!emailValid) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(com.dimension.maskbook.wallet.R.string.scene_restore_tip_invalid_email_address), color = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onConfirm.invoke()
                },
                enabled = emailValid && !buttonEnabled && email.isNotEmpty(),
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
            }
        }
    }
}

@Composable
fun EmailCodeInputModal(
    email: String,
    code: String,
    onCodeChange: (String) -> Unit,
    canSend: Boolean,
    codeValid: Boolean,
    countDown: Int,
    buttonEnabled: Boolean,
    onSendCode: () -> Unit,
    onVerify: () -> Unit,
    title: String,
    subTitle: @Composable ((ColumnScope) -> Unit)? = null,
    footer: @Composable (ColumnScope) -> Unit = {},
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6,
            )
            subTitle?.let {
                Spacer(modifier = Modifier.height(13.dp))
                subTitle.invoke(this)
            }
            Spacer(modifier = Modifier.height(21.dp))
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_backup_validation_code))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = code,
                    onValueChange = {
                        onCodeChange(it)
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.width(8.dp))
                PrimaryButton(
                    onClick = {
                        onSendCode()
                    },
                    enabled = canSend && !buttonEnabled,
                ) {
                    if (canSend) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_resend))
                    } else {
                        Text(text = countDown.toString() + "s")
                    }
                }
            }
            if (!codeValid) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(com.dimension.maskbook.wallet.R.string.scene_restore_tip_invalid_validationcode), color = Color.Red)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${stringResource(com.dimension.maskbook.wallet.R.string.scene_backup_tips_email)} $email"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onVerify()
                },
                enabled = code.isNotEmpty() && !buttonEnabled && codeValid,
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
            }
            footer.invoke(this)
        }
    }
}
