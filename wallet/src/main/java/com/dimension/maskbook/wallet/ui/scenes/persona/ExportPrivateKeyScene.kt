package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.persona.ExportPrivateKeyViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun ExportPrivateKeyScene(
    onBack: () -> Unit,
) {
    val viewModel = getViewModel<ExportPrivateKeyViewModel>()
    val text by viewModel.privateKey.observeAsState(initial = "")
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton {
                            onBack.invoke()
                        }
                    },
                    title = {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_persona_export_private_key_title))
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                text?.let { it1 ->
                    OutlinedTextField(
                        value = it1,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This export is only for exporting private key. \n" +
                            "We do not export any other data. If you need more\n" +
                            "data, please go to Settings: Backup and Recovery"
                )
                Spacer(modifier = Modifier.weight(1f))
                Row {
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
                            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_backup_btn_copy))
                        }
                    }
                }
            }
        }
    }
}