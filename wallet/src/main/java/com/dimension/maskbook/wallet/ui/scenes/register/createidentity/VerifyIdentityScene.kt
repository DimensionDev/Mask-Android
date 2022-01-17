package com.dimension.maskbook.wallet.ui.scenes.register.createidentity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import kotlin.math.absoluteValue
import kotlin.math.max

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
@Preview
fun VerifyIdentityScene(
    words: List<String>,
    title: String = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_identify_verify_title),
    subTitle: String = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_identify_verify_description),
    selectedWords: List<String>,
    onWordSelected: (String) -> Unit,
    onWordDeselected: (String) -> Unit,
    correct: Boolean,
    canConfirm: Boolean = words.size == selectedWords.size,
    onConfirm: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    title = {
                        Text(text = title)
                    },
                    navigationIcon = {
                        MaskBackButton {
                            onBack.invoke()
                        }
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
                LaunchedEffect(selectedWords.size) {
                    snapshotFlow { selectedWords.size }
                        .collect {
                            pagerState.animateScrollToPage(max(0, it - 1))
                        }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxWidth(),
                        state = pagerState,
                        itemSpacing = 18.dp,
                        count = words.size,
                    ) { page ->
                        Card(
                            modifier = Modifier
                                .graphicsLayer {
                                    val pageOffset =
                                        calculateCurrentOffsetForPage(page).absoluteValue
                                    lerp(
                                        start = 0.85f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    ).also { scale ->
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    alpha = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                                }
                                .fillMaxWidth(0.5f),
                            backgroundColor = if (page == pagerState.currentPage) {
                                MaterialTheme.colors.primary
                            } else {
                                Color(0XFFEBF0F8)
                            },
                            elevation = 0.dp,
                            onClick = {
                                val item = selectedWords.getOrNull(page)
                                if (!item.isNullOrEmpty()) {
                                    onWordDeselected.invoke(item)
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 30.dp, vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                val item = selectedWords.getOrNull(page)
                                Text(
                                    text = item ?: "", style = MaterialTheme.typography.h4,
                                    color = contentColorFor(
                                        backgroundColor = if (page == pagerState.currentPage) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            Color(0XFFEBF0F8)
                                        }
                                    )
                                )
                                Spacer(modifier = Modifier.height(26.dp))
                                Box(
                                    modifier = Modifier
                                        .height(1.dp)
                                        .background(
                                            contentColorFor(
                                                backgroundColor = if (page == pagerState.currentPage) {
                                                    MaterialTheme.colors.primary
                                                } else {
                                                    Color(0XFFEBF0F8)
                                                }
                                            )
                                        )
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row {
                        Text(
                            text = selectedWords.size.toString(),
                            style = LocalTextStyle.current.copy(color = MaterialTheme.colors.primary)
                        )
                        Text(text = "/")
                        Text(text = words.size.toString())
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!canConfirm) {
                        Text(
                            text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_mnemonic_verify_mnemonic_prompt),
                            style = LocalTextStyle.current.copy(
                                Color(0XFFFFB915)
                            )
                        )
                    } else if (!correct) {
                        Text(
                            text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_identify_verify_identity_error),
                            style = LocalTextStyle.current.copy(
                                Color(0XFFFF5F5F)
                            )
                        )
                    }
                }
                if (!canConfirm) {
                    LazyColumn {
                        itemsGridIndexed(words, rowSize = 4, spacing = 14.dp) { index, it ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = 0.dp,
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    if (!selectedWords.contains(it)) {
                                        onWordSelected.invoke(it)
                                    }
                                },
                                backgroundColor = if (selectedWords.contains(it)) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.surface
                                },
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .align(Alignment.Center),
                                    text = it,
                                    color = contentColorFor(
                                        backgroundColor = if (selectedWords.contains(it)) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            MaterialTheme.colors.surface
                                        }
                                    )
                                )
                            }
                        }
                    }
                } else {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onConfirm.invoke() },
                        enabled = correct,
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SecondaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onClear.invoke() },
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_clear))
                    }
                }
            }
        }
    }
}