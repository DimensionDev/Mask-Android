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
package com.dimension.maskbook.labs.data

import com.dimension.maskbook.common.ext.execute
import com.dimension.maskbook.extension.export.ExtensionServices
import org.koin.core.annotation.Single

@Single
internal class JSMethod(
    private val extensionServices: ExtensionServices,
) {
    suspend fun isPluginEnabled(pluginID: String): Boolean {
        return extensionServices.execute(
            "app_isPluginEnabled",
            "pluginID" to pluginID
        ) ?: false
    }

    suspend fun setPluginStatus(
        pluginID: String,
        enabled: Boolean,
    ) {
        extensionServices.execute<Unit>(
            "app_setPluginStatus",
            "pluginID" to pluginID,
            "enabled" to enabled,
        )
    }
}
