package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.*

enum class BackupType {
    Keystore,
    PrivateKey,
}

@Composable
fun BackupWalletScene(
    keyStore: String,
    privateKey: String,
    onBack: () -> Unit,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    elevation = 0.dp,
                    title = {
                        Text(text = "Back Up wallet")
                    }
                )
            }
        ) {
            Column {
                var selectedTabIndex by remember {
                    mutableStateOf(0)
                }
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = MaterialTheme.colors.background,
                    divider = {
                        TabRowDefaults.Divider(thickness = 0.dp)
                    },
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .height(3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(0.1f)
                                    .fillMaxHeight()
                                    .background(
                                        color = MaterialTheme.colors.primary,
                                        shape = RoundedCornerShape(99.dp)
                                    )
                            )
                        }
                    },
                ) {
                    BackupType.values().forEachIndexed { index, backupType ->
                        Tab(
                            text = { Text(backupType.name) },
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                            },
                            selectedContentColor = MaterialTheme.colors.primary,
                            unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                                alpha = ContentAlpha.medium
                            ),
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(ScaffoldPadding)
                        .weight(1f)
                ) {
                    val content = remember(selectedTabIndex, keyStore, privateKey) {
                        when (BackupType.values()[selectedTabIndex]) {
                            BackupType.Keystore -> keyStore
                            BackupType.PrivateKey -> privateKey
                        }
                    }
                    OutlinedTextField(
                        value = content,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                    val text = remember(selectedTabIndex) {
                        when (BackupType.values()[selectedTabIndex]) {
                            BackupType.Keystore -> "Please remember your current password. Your current login password is required for decryption when using the wallet Keystore for recovery."
                            BackupType.PrivateKey -> androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_backup_private_key_tips)
                        }
                    }
                    Text(
                        text = text,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        color = Color(0xFF1C68F3)
                    )
                    Row {
                        SecondaryButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_cancel))
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        val clipboardManager = LocalClipboardManager.current
                        PrimaryButton(
                            onClick = {
                                clipboardManager.setText(buildAnnotatedString { append(content) })
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_backup_btn_copy))
                        }
                    }
                }
            }
        }
    }
}