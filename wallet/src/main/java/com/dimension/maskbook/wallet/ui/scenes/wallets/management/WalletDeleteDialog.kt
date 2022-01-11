package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.ui.widget.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WalletDeleteDialog(
    walletData: WalletData,
    biometricEnabled:Boolean,
    password: String,
    onPasswordChanged: (String) -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    passwordValid: Boolean,
) {
    MaskDialog(
        // workaround for https://issuetracker.google.com/issues/194911971
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(horizontal = 23.dp),
        onDismissRequest = onBack,
        icon = {
            WalletAvatar(
                modifier = Modifier.size(36.dp),
                walletData = walletData
            )
        },
        title = {
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_edit_item_delete))
        },
        text = {
            Column {
                Text(text = walletData.address)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_delete_content))
            }
        },
        buttons = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(visible = !biometricEnabled) {
                    Text(text = "Payment password")
                    MaskPasswordInputField(value = password, onValueChange = onPasswordChanged)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_cancel))
                    }
                    PrimaryButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        enabled = passwordValid || biometricEnabled
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_personas_action_delete))
                    }
                }
            }
        }
    )
}