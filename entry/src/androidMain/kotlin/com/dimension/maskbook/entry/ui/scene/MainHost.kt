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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.getAll
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeHomeComposable
import com.dimension.maskbook.common.route.navigationComposeHomeComposablePackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavController
import kotlin.math.max

private val tabOrder = listOf(
    CommonRoute.Main.Tabs.Persona,
    CommonRoute.Main.Tabs.Wallet,
    CommonRoute.Main.Tabs.Labs,
    CommonRoute.Main.Tabs.Setting,
).withIndex().associate { it.value to it.index }

private val Colors.tabBackground: Color
    @Composable
    get() = if (isLight) {
        MaterialTheme.colors.surface
    } else {
        MaterialTheme.colors.background
    }

@NavGraphDestination(
    route = CommonRoute.Main.Home.path,
    deeplink = [Deeplinks.Main.Home.path],
    packageName = navigationComposeHomeComposablePackage,
    functionName = navigationComposeHomeComposable
)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainHost(
    navController: NavController,
    @Path("initialRoute") initialTab: String,
) {
    val tabs = getAll<TabScreen>().sortedBy {
        tabOrder[it.route]
    }

    val initialPage = remember(initialTab) {
        if (initialTab.isEmpty()) return@remember 0
        val index = tabs.indexOfFirst { it.route == initialTab }
        if (index != -1) index else 0
    }
    val pagerState = rememberPagerState(initialPage = initialPage)
    val scope = rememberCoroutineScope()

    MaskScene(
        navigationBarColorProvider = {
            MaterialTheme.colors.tabBackground
        }
    ) {
        MaskScaffold(
            bottomBar = {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.tabBackground)
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Layout(
                        content = {
                            tabs.forEachIndexed { index, screen ->
                                BottomNavigationItem(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        scope.launch {
                                            pagerState.scrollToPage(index)
                                        }
                                    },
                                    text = {
                                        Text(stringResource(screen.title))
                                    },
                                    icon = {
                                        Icon(
                                            painterResource(id = screen.icon),
                                            contentDescription = null
                                        )
                                    },
                                    selectedContentColor = MaterialTheme.colors.primary,
                                    unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
                                )
                            }
                        }
                    ) { measurables, constraints ->
                        val placeables = measurables.map { it.measure(constraints) }
                        val maxWidth = placeables.maxOf { it.measuredWidth }
                        layout(maxWidth * placeables.size, constraints.maxHeight) {
                            placeables.forEachIndexed { index, placeable ->
                                placeable.placeRelative(index * maxWidth, 0)
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            HorizontalPager(
                contentPadding = innerPadding,
                count = tabs.size,
                state = pagerState,
            ) {
                tabs.elementAt(it).Content(
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean = true,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = ContentAlpha.medium),
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
) {
    val color = if (selected) {
        selectedContentColor
    } else {
        unselectedContentColor
    }

    MaskButton(
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            contentColor = color,
        )
    ) {
        Layout(
            modifier = modifier
                .fillMaxHeight()
                .padding(horizontal = 9.dp, vertical = 6.dp),
            content = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProvideTextStyle(LocalTextStyle.current.copy(color = color), text)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(color, shape = CircleShape)
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    icon.invoke()
                }
            }
        ) { measurables, constraints ->
            val textMeasurable = measurables[0].measure(constraints)
            val iconMeasurable = measurables[1].measure(constraints)
            val width = max(textMeasurable.measuredWidth, iconMeasurable.measuredWidth)
            layout(width, constraints.maxHeight) {
                if (selected) {
                    textMeasurable.placeRelative(
                        ((width.toFloat() - textMeasurable.measuredWidth.toFloat()) / 2f).toInt(),
                        0,
                    )
                } else {
                    iconMeasurable.placeRelative(
                        ((width.toFloat() - iconMeasurable.measuredWidth.toFloat()) / 2f).toInt(),
                        0,
                    )
                }
            }
        }
    }
}
