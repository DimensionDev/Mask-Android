package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskInputField
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton
import com.dimension.maskbook.wallet.viewmodel.persona.ExportPrivateKeyViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun ExportPrivateKeyScene(
    onBack: () -> Unit,
) {
    val viewModel = getViewModel<ExportPrivateKeyViewModel>()
    val text by viewModel.privateKey.observeAsState(initial = "")

    val annotatedText = buildAnnotatedString {
        append(stringResource(R.string.scene_persona_export_private_key_tips))
        withStyle(style = SpanStyle(color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold)) {
            append(stringResource(R.string.scene_persona_export_private_key_backup_recovery))
        }
    }

    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar(
                navigationIcon = {
                    MaskBackButton {
                        onBack.invoke()
                    }
                },
                title = {
                    Text(text = stringResource(R.string.scene_persona_export_private_key_title))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScaffoldPadding),
        ) {
            MaskInputField(
                value = text,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = annotatedText)
            Spacer(modifier = Modifier.weight(1f))
            Row {
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
                    val manager = LocalClipboardManager.current
                    PrimaryButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            manager.setText(annotatedString = buildAnnotatedString {
                                this.append(
                                    text
                                )
                            })
                        },
                    ) {
                        Text(text = stringResource(R.string.scene_wallet_backup_btn_copy))
                    }
                }
            }
        }
    }
}