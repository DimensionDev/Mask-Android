package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton

@Composable
fun UnlockWalletDialog(
    unlockError: String?,
    onCancel: () -> Unit,
    onUnlock: (String) -> Unit,
) {
    var password by remember {
        mutableStateOf("")
    }
    MaskDialog(
        onDismissRequest = { /*TODO*/ },
        title = {
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_unlock_title))
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_general_setup_payment_password))
                Spacer(modifier = Modifier.height(8.dp))
                MaskPasswordInputField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (!unlockError.isNullOrEmpty()) {
                    Text(text = unlockError, color = MaterialTheme.colors.error)
                }
            }
        },
        buttons = {
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCancel.invoke()
                    }
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onUnlock.invoke(password)
                    },
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_unlock_button))
                }
            }
        }
    )
}