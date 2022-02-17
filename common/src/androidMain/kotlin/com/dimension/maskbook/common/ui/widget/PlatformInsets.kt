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
package com.dimension.maskbook.common.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.insets.HorizontalSide
import com.google.accompanist.insets.Insets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.navigationBarsWidth
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

data class NativeInsetsControl(
    val extendToTop: Boolean = false,
    val extendToBottom: Boolean = false,
    val extendToStart: Boolean = false,
    val extendToEnd: Boolean = false,
    val darkTheme: Boolean = false,
)

data class NativeInsetsColor(
    val top: Color = Color.Transparent,
    val bottom: Color = Color.Transparent,
    val start: Color = Color.Transparent,
    val end: Color = Color.Transparent,
)

val LocalWindowInsetsController =
    staticCompositionLocalOf<WindowInsetsControllerCompat> { error("No WindowInsetsControllerCompat") }

fun Modifier.topInsetsPadding(): Modifier = this.statusBarsPadding()
fun Modifier.bottomInsetsPadding(): Modifier = this.navigationBarsPadding()
fun Modifier.startInsetsPadding(): Modifier = this
fun Modifier.endInsetsPadding(): Modifier = this

fun Modifier.topInsetsHeight(): Modifier = this.statusBarsHeight()
fun Modifier.bottomInsetsHeight(): Modifier = this.navigationBarsHeight()
fun Modifier.startInsetsWidth(): Modifier = this.navigationBarsWidth(HorizontalSide.Left)
fun Modifier.endInsetsWidth(): Modifier = this.navigationBarsWidth(HorizontalSide.Right)

@Composable
fun PlatformInsets(
    control: NativeInsetsControl,
    color: NativeInsetsColor,
    content: @Composable () -> Unit,
) {
    val darkTheme = control.darkTheme
    val windowInsetsController = LocalWindowInsetsController.current
    LaunchedEffect(darkTheme) {
        windowInsetsController.isAppearanceLightStatusBars = !darkTheme
        windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
    }
    Box {
        val actual = provideSystemInsets(
            control.extendToBottom,
            control.extendToTop,
        )
        Box(
            modifier = Modifier
                .padding(
                    actual.let {
                        with(LocalDensity.current) {
                            val layoutDirection = LocalLayoutDirection.current
                            PaddingValues(
                                top = it.top.toDp(),
                                bottom = it.bottom.toDp(),
                                start = when (layoutDirection) {
                                    LayoutDirection.Ltr -> it.left.toDp()
                                    LayoutDirection.Rtl -> it.right.toDp()
                                },
                                end = when (layoutDirection) {
                                    LayoutDirection.Ltr -> it.right.toDp()
                                    LayoutDirection.Rtl -> it.left.toDp()
                                },
                            )
                        }
                    }
                )
                .align(Alignment.Center)
        ) {
            content()
        }
        Spacer(
            modifier = if (!control.extendToTop) {
                Modifier
                    .statusBarsHeight()
                    .navigationBarsPadding(bottom = false)
                    .zIndex(999F)
                    .fillMaxWidth()
                    .background(color.top)
            } else {
                Modifier
            }.align(Alignment.TopCenter)
        )
        Spacer(
            modifier = if (!control.extendToStart) {
                Modifier
                    .navigationBarsWidth(HorizontalSide.Left)
                    .zIndex(999F)
                    .fillMaxHeight()
                    .background(color.start)
            } else {
                Modifier
            }.align(Alignment.CenterStart)
        )
        Spacer(
            modifier = if (!control.extendToEnd) {
                Modifier
                    .navigationBarsWidth(HorizontalSide.Right)
                    .fillMaxHeight()
                    .zIndex(999F)
                    .background(color.end)
            } else {
                Modifier
            }.align(Alignment.CenterEnd)
        )
        Spacer(
            modifier = if (!control.extendToBottom) {
                Modifier
                    .navigationBarsHeight()
                    .zIndex(999F)
                    .fillMaxWidth()
                    .background(color.bottom)
            } else {
                Modifier
            }.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ImeVisibleWithInsets(
    filter: ((Boolean) -> Boolean)?,
    collectIme: ((Boolean) -> Unit)?
) {
    val ime = LocalWindowInsets.current.ime
    LaunchedEffect(ime) {
        snapshotFlow { ime.isVisible }
            .distinctUntilChanged()
            .filter { filter?.invoke(it) ?: false }
            .collect {
                collectIme?.invoke(it)
            }
    }
}

@Composable
fun ImeHeightWithInsets(
    filter: ((Int) -> Boolean)?,
    collectIme: ((Int) -> Unit)?
) {
    val ime = LocalWindowInsets.current.ime

    LaunchedEffect(ime) {
        snapshotFlow { ime.bottom }
            .distinctUntilChanged()
            .filter { filter?.invoke(it) ?: false }
            .collect {
                collectIme?.invoke(it)
            }
    }
}

@Composable
fun imeBottomInsets(): Dp {
    val navigation = LocalWindowInsets.current.navigationBars
    val ime = LocalWindowInsets.current.ime
    ime.bottom.coerceAtLeast(navigation.bottom)
    return with(LocalDensity.current) {
        ime.bottom.coerceAtLeast(navigation.bottom).toDp()
    }
}

@Composable
private fun provideSystemInsets(
    extendViewIntoNavigationBar: Boolean,
    extendViewIntoStatusBar: Boolean
): Insets {
    val ime = LocalWindowInsets.current.ime
    val navigation = LocalWindowInsets.current.navigationBars
    val status = LocalWindowInsets.current.statusBars
    return key(
        ime,
        ime.isVisible,
        navigation,
        status
    ) {
        ime.copy(
            left = if (extendViewIntoNavigationBar) {
                0
            } else {
                ime.left.coerceAtLeast(navigation.left)
            },
            right = if (extendViewIntoNavigationBar) {
                0
            } else {
                ime.right.coerceAtLeast(navigation.right)
            },
            bottom = if (extendViewIntoNavigationBar) {
                0
            } else {
                ime.bottom.coerceAtLeast(navigation.bottom)
            },
            top = if (extendViewIntoNavigationBar) {
                0
            } else {
                ime.top.coerceAtLeast(navigation.top)
            } + if (extendViewIntoStatusBar) {
                0
            } else {
                status.top
            },
        )
    }
}
