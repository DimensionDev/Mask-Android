package com.dimension.maskbook.wallet.ui.scenes.register.recovery.local

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskPasswordInputField
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
            Text(text = stringResource(R.string.scene_set_backup_password_backup_password))
            Spacer(modifier = Modifier.height(8.dp))
            MaskPasswordInputField(
                value = password,
                onValueChange = onPasswordChanged,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            ) {
                Text(text = stringResource(R.string.common_controls_next))
            }
        }
    }
}