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
package com.dimension.maskbook.labs.repository

import com.dimension.maskbook.labs.data.JSMethod
import com.dimension.maskbook.labs.export.model.AppData
import com.dimension.maskbook.labs.export.model.AppKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class AppRepository(
    private val jsMethod: JSMethod,
) : IAppRepository {

    private val _apps = MutableStateFlow(
        AppKey.values().map { AppData(it, true) }
    )

    override val apps = _apps.asSharedFlow()

    private suspend fun refreshApps() {
        _apps.value = _apps.value.let { apps ->
            apps.map {
                it.copy(enabled = jsMethod.isPluginEnabled(it.key.id))
            }
        }
    }

    override suspend fun setEnabled(appKey: AppKey, enabled: Boolean) {
        jsMethod.setPluginStatus(appKey.id, enabled)
        refreshApps()
    }

    override suspend fun init() {
        refreshApps()
    }
}
