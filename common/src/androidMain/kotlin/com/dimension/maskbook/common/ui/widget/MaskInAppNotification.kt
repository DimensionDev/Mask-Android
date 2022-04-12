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

import android.util.Log
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.dimension.maskbook.common.ui.notification.EventActionContext
import com.dimension.maskbook.common.ui.notification.InAppNotification
import com.dimension.maskbook.common.ui.notification.NotificationWithActionEvent

val LocalInAppNotification = compositionLocalOf { InAppNotification() }

@Composable
fun MaskInAppNotification(
    snackBarHostState: SnackbarHostState,
) {
    val inAppNotification = LocalInAppNotification.current
    Log.d("MaskInAppNotification", "inAppNotification: $inAppNotification")
    val notification by inAppNotification.collectAsState(null)
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
