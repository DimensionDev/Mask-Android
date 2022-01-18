package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun RenamePersona(
    name: String,
    onNameChanged: (String) -> Unit,
    onDone: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding),
        ) {
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_personas_action_rename))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { onNameChanged.invoke(it) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDone.invoke()
                },
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
            }
        }
    }
}