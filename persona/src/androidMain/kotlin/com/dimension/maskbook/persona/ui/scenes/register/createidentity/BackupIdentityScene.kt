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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.common.ui.widget.MaskCard
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.SingleLineText
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
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
    BackupContent(
        words = words,
        onRefreshWords = onRefreshWords,
        onBack = onBack,
        onDownload = onDownload,
        onSkipOrNext = onSkipOrNext,
        showNext = showNext
    )
}

@OptIn(ExperimentalFoundationApi::class, androidx.compose.material.ExperimentalMaterialApi::class)
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
                        Text(text = stringResource(R.string.scene_identity_create_title))
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
                            MaskIconButton(onClick = onRefreshWords) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primary,
                                )
                            }
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
                    itemsGridIndexed(words, rowSize = 2, spacing = 8.dp) { index, it ->
                        MaskCard(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 0.dp,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${index + 1}",
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFFB4B8C8),
                                    style = MaterialTheme.typography.h5,
                                    modifier = Modifier.width(20.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                SingleLineText(
                                    text = it,
                                    lineHeight = 18.sp,
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                    item {
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_warn_blue),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Identity code is used to generate your persona.",
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.weight(1f),
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
