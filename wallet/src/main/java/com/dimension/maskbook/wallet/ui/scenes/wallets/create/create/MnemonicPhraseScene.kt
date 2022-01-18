package com.dimension.maskbook.wallet.ui.scenes.wallets.create.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.MnemonicWarningDialog
import com.dimension.maskbook.wallet.ui.widget.*

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun MnemonicPhraseScene(
    words: List<String>,
    onRefreshWords: () -> Unit,
    onVerify: () -> Unit,
    onBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onRefreshWords.invoke()
    }
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                )
            }
        ) {
            Box {
                var showWarning by remember {
                    mutableStateOf(true)
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ScaffoldPadding),
                ) {
                    Text(text = stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_mnemonic_title), style = MaterialTheme.typography.h4)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_identity_create_description),
                        )
                        Icon(
                            Icons.Default.Refresh,
                            modifier = Modifier.clickable {
                                onRefreshWords.invoke()
                            },
                            contentDescription = null,
                            tint = Color(0XFF1C68F3)
                        )
                    }
                    Spacer(modifier = Modifier.height(26.dp))
                    PhraseContent(
                        modifier = Modifier.weight(1f),
                        words = words// todo Logic: pass words
                    )
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onVerify.invoke()
                        },
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_verify))
                    }
                }

                if (showWarning) {
                    MnemonicWarningDialog {
                        showWarning = false
                    }
                }
            }
        }
    }
}

@Composable
private fun PhraseContent(
    modifier: Modifier = Modifier,
    words: List<String>
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsGridIndexed(words, rowSize = 3, spacing = 8.dp) { index, it ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = "$index $it",
                )
            }
        }
    }
}