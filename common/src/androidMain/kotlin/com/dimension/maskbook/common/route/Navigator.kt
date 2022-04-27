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
package com.dimension.maskbook.common.route

import com.dimension.maskbook.common.ui.notification.Event
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface NavigateArgs

class DeeplinkNavigateArgs(val url: String) : NavigateArgs
class RouteNavigateArgs(val route: String) : NavigateArgs

object Navigator {
    private val _navigateEvent = MutableSharedFlow<Event<NavigateArgs>>(extraBufferCapacity = 10, replay = 1)
    val navigateEvent = _navigateEvent.asSharedFlow()

    fun navigate(route: String) {
        _navigateEvent.tryEmit(Event(RouteNavigateArgs(route)))
    }

    fun deeplink(url: String) {
        _navigateEvent.tryEmit(Event(DeeplinkNavigateArgs(url)))
    }
}
