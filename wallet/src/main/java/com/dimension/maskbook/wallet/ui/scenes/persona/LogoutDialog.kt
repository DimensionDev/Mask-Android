package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton
import org.koin.androidx.compose.get

@Composable
fun LogoutDialog(
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = { /*TODO*/ },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.common_alert_persona_logout_title))
        },
        text = {
            Text(text = stringResource(R.string.common_alert_persona_logout_description))
        },
        buttons = {
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
                val repository = get<IPersonaRepository>()
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        repository.logout()
                        onDone.invoke()
                    },
                ) {
                    Text(text = stringResource(R.string.common_controls_confirm))
                }
            }
        }
    )
}