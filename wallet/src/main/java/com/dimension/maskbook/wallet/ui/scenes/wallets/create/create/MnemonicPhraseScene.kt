/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.ui.scenes.wallets.create.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.MnemonicWarningDialog
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskIconButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.ui.widget.itemsGridIndexed

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
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = stringResource(R.string.scene_wallet_mnemonic_title))
                    }
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.scene_identity_create_description),
                            style = MaterialTheme.typography.subtitle1,
                        )
                        MaskIconButton(onClick = onRefreshWords) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(26.dp))
                    PhraseContent(
                        modifier = Modifier.weight(1f),
                        words = words // todo Logic: pass words
                    )
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onVerify.invoke()
                        },
                    ) {
                        Text(text = stringResource(R.string.common_controls_verify))
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
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colors.surface, shape = MaterialTheme.shapes.small)
                    .height(52.dp)
                    .padding(horizontal = 15.dp, vertical = 0.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${index + 1}",
                    textAlign = TextAlign.Center,
                    color = Color(0xFFB4B8C8),
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.width(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhraseContentPreview() {
    MaskTheme {
        PhraseContent(
            words = listOf(
                "mutual", "mutual", "mutual",
                "mutual", "mutual", "mutual",
                "mutual", "mutual", "mutualMutual",
            )
        )
    }
}
