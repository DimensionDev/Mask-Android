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
package com.dimension.maskbook.common.ui.notification

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dimension.maskbook.common.ext.observeAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

interface NotificationEvent {
    @Composable
    fun getMessage(): String
}

// provide context for notification in future
class EventActionContext

interface NotificationWithActionEvent : NotificationEvent {
    @Composable
    fun getActionMessage(): String
    val action: EventActionContext.() -> Unit
}

class StringNotificationEvent(
    private val message: String,
) : NotificationEvent {
    @Composable
    override fun getMessage(): String {
        return message
    }

    companion object {
        fun InAppNotification.show(message: String) {
            show(StringNotificationEvent(message = message))
        }
    }
}

open class StringResNotificationEvent(
    @StringRes val message: Int,
) : NotificationEvent {
    @Composable
    override fun getMessage(): String {
        return stringResource(message)
    }

    companion object {
        fun InAppNotification.show(@StringRes message: Int) {
            show(StringResNotificationEvent(message = message))
        }
    }
}

class StringWithActionNotificationEvent(
    private val message: String,
    private val actionMessage: String,
    override val action: EventActionContext.() -> Unit,
) : NotificationWithActionEvent {
    @Composable
    override fun getActionMessage(): String {
        return actionMessage
    }

    @Composable
    override fun getMessage(): String {
        return message
    }

    companion object {
        fun InAppNotification.show(message: String, actionMessage: String, action: EventActionContext.() -> Unit) {
            show(StringWithActionNotificationEvent(message = message, actionMessage = actionMessage, action = action))
        }
    }
}

class InAppNotification {
    private val _source = MutableStateFlow<Event<NotificationEvent?>?>(null)
    val source
        get() = _source.asSharedFlow()

    fun show(event: NotificationEvent) {
        _source.value = ((Event(event)))
    }

    @Composable
    fun observeAsState(initial: Event<NotificationEvent?>? = null) =
        source.observeAsState(initial = initial)
}
