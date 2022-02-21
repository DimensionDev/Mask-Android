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
package com.dimension.maskbook.wallet.ui.scenes.register.createidentity

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.dimension.maskbook.common.ui.widget.MaskCard
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.common.ui.widget.itemsGridIndexed
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.model.MnemonicWord
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun VerifyIdentityScene(
    words: List<MnemonicWord>,
    title: String = stringResource(R.string.scene_identify_verify_title),
    subTitle: String = stringResource(R.string.scene_identify_verify_description),
    selectedWords: List<MnemonicWord>,
    onWordSelected: (MnemonicWord) -> Unit,
    onWordDeselected: (MnemonicWord) -> Unit,
    correct: Boolean,
    canConfirm: Boolean = words.size == selectedWords.size,
    onConfirm: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)

    MaskScene {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    title = {
                        Text(text = title)
                    },
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    subTitle = {
                        Text(text = subTitle)
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                val pagerState = rememberPagerState()
                if (words.isNotEmpty()) {
                    LaunchedEffect(selectedWords.size) {
                        snapshotFlow { selectedWords.size }.collect {
                            pagerState.animateScrollToPage(
                                it.coerceAtMost(words.size - 1).coerceAtLeast(0)
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    PhrasePager(
                        pagerState = pagerState,
                        words = words,
                        selectedWords = selectedWords,
                        onPagerClick = {
                            val item = selectedWords.getOrNull(it)
                            item?.let {
                                onWordDeselected.invoke(it)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row {
                        Text(
                            text = selectedWords.size.toString(),
                            color = MaterialTheme.colors.primary,
                        )
                        Text(text = "/")
                        Text(text = words.size.toString())
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!canConfirm) {
                        Text(
                            text = stringResource(R.string.scene_mnemonic_verify_mnemonic_prompt),
                            style = LocalTextStyle.current.copy(
                                Color(0XFFFFB915)
                            )
                        )
                    } else if (!correct) {
                        Text(
                            text = stringResource(R.string.scene_identify_verify_identity_error),
                            style = LocalTextStyle.current.copy(
                                Color(0XFFFF5F5F)
                            )
                        )
                    }
                }
                if (!canConfirm) {
                    PhraseContent(
                        words = words,
                        selectedWords = selectedWords,
                        onWordClick = onWordSelected
                    )
                } else {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onConfirm,
                        enabled = correct,
                    ) {
                        Text(text = stringResource(R.string.common_controls_confirm))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SecondaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onClear,
                    ) {
                        Text(text = stringResource(R.string.common_controls_clear))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
private fun PhrasePager(
    pagerState: PagerState,
    words: List<MnemonicWord>,
    selectedWords: List<MnemonicWord>,
    onPagerClick: (Int) -> Unit,
) {
    val selectBackgroundColor = MaterialTheme.colors.primary
    val unSelectBackgroundColor = MaterialTheme.colors.surface
    val selectContentColor = contentColorFor(selectBackgroundColor)
    val unSelectContentColor = contentColorFor(unSelectBackgroundColor)

    HorizontalPager(
        state = pagerState,
        count = words.size,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(90.dp),
    ) { page ->
        var backgroundColor by remember { mutableStateOf(selectBackgroundColor) }
        var contentColor by remember { mutableStateOf(selectContentColor) }
        MaskCard(
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                    lerp(
                        start = 0.65f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }
                    lerp(
                        start = unSelectBackgroundColor,
                        stop = selectBackgroundColor,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { color ->
                        backgroundColor = color
                    }
                    lerp(
                        start = unSelectContentColor,
                        stop = selectContentColor,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { color ->
                        contentColor = color
                    }
                }
                .fillMaxWidth(),
            backgroundColor = backgroundColor,
            elevation = 0.dp,
            onClick = {
                onPagerClick(page)
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val item = selectedWords.getOrNull(page)
                Text(
                    text = item?.word ?: "",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h1,
                    color = contentColor
                )
                Spacer(
                    modifier = Modifier.height(26.dp)
                )
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .background(contentColor)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PhraseContent(
    words: List<MnemonicWord>,
    selectedWords: List<MnemonicWord>,
    onWordClick: (MnemonicWord) -> Unit,
) {
    LazyColumn {
        itemsGridIndexed(words, rowSize = 4, spacing = 14.dp) { _, word ->
            val isSelected = selectedWords.contains(word)
            val backgroundColor = if (isSelected) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.surface
            }
            MaskButton(
                onClick = { if (!isSelected) onWordClick(word) },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor
                )
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        text = word.word,
                        color = contentColorFor(backgroundColor)
                    )
                }
            }
        }
    }
}
