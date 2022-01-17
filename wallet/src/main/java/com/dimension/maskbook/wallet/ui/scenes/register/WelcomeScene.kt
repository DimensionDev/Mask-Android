package com.dimension.maskbook.wallet.ui.scenes.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun WelcomeScene(
    persona: String,
    onPersonaChanged: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.scene_persona_welcome_title))
                    },
                    subTitle = {
                        Text(text = stringResource(R.string.scene_persona_welcome_description))
                    },
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ScaffoldPadding)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = stringResource(R.string.scene_persona_welcome_persona))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = persona,
                    onValueChange = onPersonaChanged,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNext
                ) {
                    Text(text = stringResource(R.string.common_controls_next))
                }
            }
        }
    }
}