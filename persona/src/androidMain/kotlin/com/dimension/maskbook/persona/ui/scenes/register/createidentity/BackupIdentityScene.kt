/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.persona.ui.scenes.register.createidentity

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskCard
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.common.ui.widget.button.clickable
import com.dimension.maskbook.common.ui.widget.itemsGridIndexed
import com.dimension.maskbook.persona.R

@Composable
fun BackupIdentityScene(
    words: List<String>,
    showNext: Boolean = false,
    onRefreshWords: () -> Unit,
    onDownload: () -> Unit,
    onSkipOrNext: () -> Unit,
    onBack: () -> Unit,
) {
    var showDialog by rememberSaveable {
        mutableStateOf(true)
    }
    if (showDialog) {
        MaskDialog(
            onDismissRequest = {
                showDialog = false
            },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDialog = false },
                ) {
                    Text(text = stringResource(R.string.common_controls_ok))
                }
            },
            title = {
                Text(text = stringResource(R.string.common_alert_identity_phrase_title))
            },
            text = {
                Text(text = stringResource(R.string.common_alert_identity_phrase_description))
            }
        )
    }
    BackupContent(
        words = words,
        onRefreshWords = onRefreshWords,
        onBack = onBack,
        onDownload = onDownload,
        onSkipOrNext = onSkipOrNext,
        showNext = showNext
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BackupContent(
    showNext: Boolean,
    words: List<String>,
    onRefreshWords: () -> Unit,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    onSkipOrNext: () -> Unit,
) {
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.scene_identify_verify_title))
                    },
                    navigationIcon = {
                        MaskBackButton {
                            onBack.invoke()
                        }
                    },
                    subTitle = {
                        Row {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = stringResource(R.string.scene_identity_create_description),
                            )
                            Icon(
                                Icons.Default.Refresh,
                                modifier = Modifier.clickable {
                                    onRefreshWords.invoke()
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                ) {
                    itemsGridIndexed(words, rowSize = 3, spacing = 8.dp) { index, it ->
                        MaskCard(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 0.dp,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                modifier = Modifier.padding(12.dp),
                                text = "${index + 1} $it",
                            )
                        }
                    }
                }
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onDownload.invoke() },
                ) {
                    Text(text = "Download")
                }
                Spacer(modifier = Modifier.height(16.dp))
                SecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSkipOrNext.invoke() },
                ) {
                    Text(text = if (showNext) "I downloaded, next" else "Skip, download later")
                }
                Spacer(modifier = Modifier.height(54.dp))
            }
        }
    }
}
