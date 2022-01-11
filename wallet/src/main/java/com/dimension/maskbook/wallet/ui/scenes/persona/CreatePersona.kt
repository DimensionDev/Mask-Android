package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import org.koin.androidx.compose.get

@Composable
fun CreatePersona(
    onDone: () -> Unit,
) {
    val repository = get<IPersonaRepository>()
    var name by remember {
        mutableStateOf("")
    }
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding),
        ) {
            Text(text = "Create persona")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    repository.addPersona(name)
                    onDone.invoke()
                },
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_next))
            }
        }
    }
}