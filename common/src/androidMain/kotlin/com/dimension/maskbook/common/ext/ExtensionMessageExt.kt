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

import com.dimension.maskbook.extension.export.model.ExtensionMessage
import com.dimension.maskbook.extension.export.model.buildExtensionResponse
import kotlinx.serialization.json.encodeToJsonElement

inline fun <reified T> ExtensionMessage.decodeOptions(): T? {
    return params?.decodeJson<T>()
}

inline fun <reified T : Any> ExtensionMessage.responseSuccess(result: T?): Boolean {
    response(
        buildExtensionResponse(
            id = id,
            jsonrpc = jsonrpc,
            result = wrapResult(result),
        )
    )
    return true
}

inline fun <reified T : Any> wrapResult(result: T?): Any? {
    return JSON.encodeToJsonElement(result).normalized
}
