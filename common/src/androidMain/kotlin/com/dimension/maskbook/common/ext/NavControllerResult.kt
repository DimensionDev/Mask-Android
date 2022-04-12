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
package com.dimension.maskbook.common.ext

import com.dimension.maskbook.common.model.ResultEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavController
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.ViewModelStoreOwner
import moe.tlaster.precompose.viewmodel.getViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

/**
 * use for navigate result, use like:
 *
 * LaunchedEffect(Unit) {
 *    navController.getEventFlow().collect { event ->
 *    }
 * }
 * navController.sendEvent(event)
 */

fun NavController.getEventFlow(): Flow<ResultEvent> {
    return backQueue.first().eventResultViewModel.eventFlow
}

inline fun <reified T : ResultEvent> NavController.eventFlow(): Flow<T> {
    return getEventFlow().filterIsInstance()
}

fun NavController.sendEvent(event: ResultEvent) {
    backQueue.first().eventResultViewModel.setEvent(event)
}

private class EventResultViewModel : ViewModel() {

    private val _eventFlow = MutableStateFlow<ResultEvent>(ResultEvent.None)
    val eventFlow = _eventFlow.asStateFlow().filterNot { it is ResultEvent.None }

    init {
        viewModelScope.launch {
            // reset initial value after 800ms
            eventFlow.collectLatest {
                delay(800)
                _eventFlow.tryEmit(ResultEvent.None)
            }
        }
    }

    fun setEvent(value: ResultEvent) {
        _eventFlow.tryEmit(value)
    }
}

private val ViewModelStoreOwner.eventResultViewModel: EventResultViewModel
    get() = viewModelStore.getViewModel { EventResultViewModel() }
