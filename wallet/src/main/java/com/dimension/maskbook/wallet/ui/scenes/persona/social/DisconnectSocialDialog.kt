package com.dimension.maskbook.wallet.ui.scenes.persona.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton

@Composable
fun DisconnectSocialDialog(
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = { onBack.invoke() },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        text = {
            Text(text = "Are you sure you want to disconnect the Twitter account of @pineapple ? After disconnection, this account will no longer be able to decrypt and encrypt any information with Mask Network.")
        },
        buttons = {
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
                        onConfirm.invoke()
                    },
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
                }
            }
        }
    )
}