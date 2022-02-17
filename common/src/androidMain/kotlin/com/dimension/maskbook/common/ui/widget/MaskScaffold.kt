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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.notification.EventActionContext
import com.dimension.maskbook.common.ui.notification.InAppNotification
import com.dimension.maskbook.common.ui.notification.NotificationWithActionEvent

@Composable
fun MaskScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerGesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colors.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    drawerScrimColor: Color = DrawerDefaults.scrimColor,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable (PaddingValues) -> Unit
) {
    ApplyNotification(scaffoldState.snackbarHostState)
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
        drawerContent = drawerContent,
        drawerGesturesEnabled = drawerGesturesEnabled,
        drawerShape = drawerShape,
        drawerElevation = drawerElevation,
        drawerBackgroundColor = drawerBackgroundColor,
        drawerContentColor = drawerContentColor,
        drawerScrimColor = drawerScrimColor,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        content = content,
    )
}

val LocalInAppNotification = compositionLocalOf { InAppNotification() }

@Composable
fun ApplyNotification(
    snackBarHostState: SnackbarHostState
) {
    val inAppNotification = LocalInAppNotification.current
    val notification by inAppNotification.observeAsState(null)
    val event = notification?.getContentIfNotHandled()
    val message = event?.getMessage()
    val actionMessage = event?.let {
        if (it is NotificationWithActionEvent) {
            it.getActionMessage()
        } else {
            null
        }
    }
    val actionContext = remember {
        EventActionContext()
    }
    LaunchedEffect(event) {
        message?.let {
            when (
                snackBarHostState.showSnackbar(
                    message = it,
                    actionLabel = actionMessage,
                    duration = if (event is NotificationWithActionEvent) {
                        SnackbarDuration.Long
                    } else {
                        SnackbarDuration.Short
                    }
                )
            ) {
                SnackbarResult.Dismissed -> {
                }
                SnackbarResult.ActionPerformed -> {
                    if (event is NotificationWithActionEvent) {
                        event.action.invoke(actionContext)
                    }
                }
            }
        }
    }
}

val ScaffoldPadding = PaddingValues(
    top = 24.dp,
    start = 23.dp,
    end = 23.dp,
    bottom = 58.dp,
)

val TabScaffoldPadding = PaddingValues(
    top = 12.dp,
    start = 23.dp,
    end = 23.dp,
    bottom = 24.dp,
)
