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
package com.dimension.maskbook.extension.export

import com.dimension.maskbook.extension.export.model.ExtensionMessage
import com.dimension.maskbook.extension.export.model.Site
import kotlinx.coroutines.flow.Flow

interface ExtensionServices {
    val site: Flow<Site>
    fun setSite(site: Site)
    val isExtensionActive: Flow<Boolean>
    fun loadUrl(url: String)
    suspend fun ensureExtensionActive()
    suspend fun runBackgroundJSMethod(method: String, isWait: Boolean, vararg args: Pair<String, Any>): String?
    fun sendBackgroundJSEventResponse(map: Map<String, Any?>)
    fun subscribeBackgroundJSEvent(vararg method: String): Flow<ExtensionMessage>
    suspend fun runCurrentContentJSMethod(method: String, isWait: Boolean, vararg args: Pair<String, Any>): String?
    fun sendCurrentContentJSEventResponse(map: Map<String, Any?>)
    fun subscribeCurrentContentJSEvent(vararg method: String): Flow<ExtensionMessage>
}
