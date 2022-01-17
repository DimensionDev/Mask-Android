package com.dimension.maskbook.wallet.ui.scenes.wallets.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton

@Composable
fun MnemonicWarningDialog(onDismissRequest: () -> Unit) {
    MaskDialog(
        onDismissRequest = { onDismissRequest.invoke() },
        title = {
            Text(text = "Store Mnemonic Phrase safely")
        },
        text = {
            Text(text = "Your mnemonic phrase is composed of randomly selected words. Please carefully write down each word in the order it appears.")
        },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onDismissRequest.invoke() },
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_ok))
            }
        }
    )
}

@Composable
fun MnemonicScreenShotsWarningDialog() {
    val rootNavController = LocalRootNavController.current
    MaskDialog(
        onDismissRequest = { rootNavController.popBackStack() },
        title = {
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_alert_recovery_key_warning_title))
        },
        text = {
            Text(text = "Simply taking a screenshot of mnemonic phrase is not safe.")
        },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { rootNavController.popBackStack() },
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_i_understand))
            }
        }
    )
}