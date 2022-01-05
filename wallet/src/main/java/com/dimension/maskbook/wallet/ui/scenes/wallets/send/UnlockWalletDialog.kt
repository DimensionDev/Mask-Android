package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            Text(text = "Unlock Wallet")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Payment password")
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
                    Text(text = "Cancel")
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onUnlock.invoke(password)
                    },
                ) {
                    Text(text = "Unlock")
                }
            }
        }
    )
}