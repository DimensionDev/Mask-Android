package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton
import org.koin.androidx.compose.get

@Composable
fun DeleteDialog(
    onBack: () -> Unit,
) {
    val repository = get<IPersonaRepository>()
    val current by repository.currentPersona.observeAsState(initial = null)
    MaskDialog(
        onDismissRequest = { /*TODO*/ },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.scene_personas_delete))
        },
        text = {
            Text(text = stringResource(R.string.scene_personas_delete_tip))
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
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        current?.let {
                            repository.removePersona(it.id)
                        }
                        onBack.invoke()
                    },
                ) {
                    Text(text = stringResource(R.string.common_controls_confirm))
                }
            }
        }
    )
}