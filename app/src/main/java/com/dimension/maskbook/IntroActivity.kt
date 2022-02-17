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
package com.dimension.maskbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dimension.maskbook.util.setSettings
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        setContent {
            ProvideWindowInsets {
                IntroScene(
                    onStart = { complete() },
                )
            }
        }
    }

    private fun complete() {
        setSettings("is_intro_shown", true)
        startActivity(Intent(this, GeckoViewActivity::class.java))
        finish()
    }
}

private data class IntroData(
    @DrawableRes val img: Int,
    val desc: AnnotatedString,
)

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun IntroScene(
    onStart: () -> Unit,
) {
    val introList = remember {
        listOf(
            IntroData(
                img = R.drawable.ic_intro_01,
                desc = buildAnnotatedString {
                    append("Your ")
                    withStyle(SpanStyle(fontWeight = FontWeight.W800)) {
                        append("Portal")
                    }
                    append(" to the ")
                    withStyle(SpanStyle(fontWeight = FontWeight.W800)) {
                        append("New, \nOpen Internet")
                    }
                }
            ),
            IntroData(
                img = R.drawable.ic_intro_02,
                desc = buildAnnotatedString {
                    append("Send ")
                    withStyle(SpanStyle(fontWeight = FontWeight.W800)) {
                        append("encrypted")
                    }
                    append(" messages \non social media with ")
                    withStyle(SpanStyle(fontWeight = FontWeight.W800)) {
                        append("persona")
                    }
                }
            ),
            IntroData(
                img = R.drawable.ic_intro_03,
                desc = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.W800)) {
                        append("Multi-Chain wallet,")
                    }
                    append(" \ncompatible with ")
                    withStyle(SpanStyle(fontWeight = FontWeight.W800)) {
                        append("WalletConnect")
                    }
                }
            ),
            IntroData(
                img = R.drawable.ic_intro_04,
                desc = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.W800)) {
                        append("Back up in time,")
                    }
                    append("\nwith cloud / local ")
                    withStyle(SpanStyle(fontWeight = FontWeight.W800)) {
                        append("backup")
                    }
                }
            ),
        )
    }

    Box(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF1565F7), Color(0xFF4E8DFF))
                )
            )
            .fillMaxSize(),
    ) {
        val pagerState = rememberPagerState()

        HorizontalPager(
            state = pagerState,
            count = introList.size,
        ) { page ->
            Box {
                IntroPage(
                    item = introList[page],
                )
                if (page == introList.size - 1) {
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color(0xFF1C68F3),
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 29.dp,
                            vertical = 16.dp
                        ),
                        elevation = null,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 147.dp)
                            .navigationBarsPadding(),
                    ) {
                        Text(
                            text = "Let’s start",
                            fontWeight = FontWeight.W600,
                            fontSize = 18.sp,
                            lineHeight = 21.6.sp,
                        )
                    }
                } else {
                    TextButton(
                        onClick = onStart,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFFD7E6FF),
                        ),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 22.5.dp, top = 10.dp)
                            .statusBarsPadding(),
                    ) {
                        Text(
                            text = "Skip",
                            fontWeight = FontWeight.W700,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                        )
                    }
                }
            }
        }
        IntroPagerIndicator(
            pagerState = pagerState,
            inactiveColor = Color(0xFFD7E6FF),
            indicatorWidth = 10.dp,
            indicatorHeight = 6.dp,
            activeColor = Color.White,
            activeIndicatorWidth = 20.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 58.dp)
                .navigationBarsPadding(),
        )
    }
}

@Composable
private fun IntroPage(
    item: IntroData,
) {
    Box(
        modifier = Modifier
            .padding(bottom = 64.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_intro_grid),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth(),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 36.5.dp),
        ) {
            Image(
                painter = painterResource(item.img),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(80.dp))
            Text(
                text = item.desc,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.W400,
                fontSize = 18.sp,
                lineHeight = 27.sp,
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun IntroPagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    inactiveColor: Color = activeColor.copy(ContentAlpha.disabled),
    indicatorWidth: Dp = 8.dp,
    indicatorHeight: Dp = indicatorWidth,
    activeIndicatorWidth: Dp = indicatorWidth,
    activeIndicatorHeight: Dp = indicatorHeight,
    spacing: Dp = indicatorWidth,
    indicatorShape: Shape = CircleShape,
) {

    val indicatorWidthPx = LocalDensity.current.run { indicatorWidth.roundToPx() }
    val activeIndicatorWidthPx = LocalDensity.current.run { activeIndicatorWidth.roundToPx() }
    val spacingPx = LocalDensity.current.run { spacing.roundToPx() }

    val scrollPosition = (pagerState.currentPage + pagerState.currentPageOffset)
        .coerceIn(0f, (pagerState.pageCount - 1).coerceAtLeast(0).toFloat())

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pagerState.pageCount) { index ->
                val isSelected = scrollPosition.toInt() == index
                val animeWidth by animateDpAsState(if (isSelected) activeIndicatorWidth else indicatorWidth)
                val animeHeight by animateDpAsState(if (isSelected) activeIndicatorHeight else indicatorHeight)
                Box(
                    modifier = Modifier
                        .size(animeWidth, animeHeight)
                        .background(color = inactiveColor, shape = indicatorShape)
                )
            }
        }

        Box(
            Modifier
                .offset {
                    IntOffset(
                        x = ((spacingPx + indicatorWidthPx) * (scrollPosition - 1) + activeIndicatorWidthPx).toInt(),
                        y = 0
                    )
                }
                .size(width = activeIndicatorWidth, height = activeIndicatorHeight)
                .background(
                    color = activeColor,
                    shape = indicatorShape,
                )
        )
    }
}
