package com.dimension.maskbook.wallet.ui.scenes.register.recovery.local

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun BackupPasswordInputModal(
    password: String,
    onPasswordChanged: (String) -> Unit,
    onNext: () -> Unit,
    enabled: Boolean = true,
) {
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding)
        ) {
            Text(text = "Backup password")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChanged,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            ) {
                Text(text = "Next")
            }
        }
    }
}