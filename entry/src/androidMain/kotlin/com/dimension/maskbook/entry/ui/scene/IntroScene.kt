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
package com.dimension.maskbook.entry.ui.scene

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.entry.R
import com.dimension.maskbook.entry.repository.EntryRepository
import com.dimension.maskbook.entry.route.EntryRoute
import com.dimension.maskbook.persona.route.PersonaRoute
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import moe.tlaster.precompose.navigation.NavController
import org.koin.androidx.compose.get

private data class IntroData(
    @DrawableRes val img: Int,
    val desc: String,
)

@NavGraphDestination(
    route = EntryRoute.Intro,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable
)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun IntroScene(
    navController: NavController,
) {
    val repository = get<EntryRepository>()
    val introList = remember {
        listOf(
            IntroData(
                img = R.drawable.ic_intro_01,
                desc = "Your Portal to the New,\nOpen Internet"
            ),
            IntroData(
                img = R.drawable.ic_intro_02,
                desc = "Send Encrypted Messages \non Social Networks with Persona"
            ),
            IntroData(
                img = R.drawable.ic_intro_03,
                desc = "Multi-Chain Wallet, \nCompatible with WalletConnect"
            ),
            IntroData(
                img = R.drawable.ic_intro_04,
                desc = "Back up in Time,\nwith Cloud / Local Backup"
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
                val isEnd = page == introList.size - 1
                IntroPage(
                    item = introList[page],
                    isEnd = isEnd,
                    onStartClick = {
                        repository.setShouldShowEntry(false)
                        navController.navigate(PersonaRoute.Register.Init) {
                            popUpTo(EntryRoute.Intro) {
                                inclusive = true
                            }
                        }
                    }
                )
                if (!isEnd) {
                    TextButton(
                        onClick = {
                            repository.setShouldShowEntry(false)
                            navController.navigate(PersonaRoute.Register.Init) {
                                popUpTo(EntryRoute.Intro) {
                                    inclusive = true
                                }
                            }
                        },
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
                            color = Color(0xFFD7E6FF),
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
    isEnd: Boolean,
    onStartClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(bottom = 64.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IntroImage(item.img)
            Spacer(Modifier.height(80.dp))
            Text(
                text = item.desc,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight(860),
                fontSize = 18.sp,
                lineHeight = 27.sp,
            )
            Spacer(Modifier.height(24.dp))
            if (isEnd) {
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 29.dp,
                    ),
                    elevation = null,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(54.dp),
                ) {
                    Text(
                        text = "Let’s start",
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp,
                        lineHeight = 21.6.sp,
                        color = Color(0xFF1C68F3),
                    )
                }
            } else {
                Spacer(Modifier.height(54.dp))
            }
        }
    }
}

@Composable
private fun IntroImage(@DrawableRes imageId: Int) {
    Box {
        Image(
            painter = painterResource(R.drawable.ic_intro_grid),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .matchParentSize()
                .padding(top = 80.dp),
        )
        Image(
            painter = painterResource(imageId),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(horizontal = 36.5.dp)
                .fillMaxWidth(),
        )
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
