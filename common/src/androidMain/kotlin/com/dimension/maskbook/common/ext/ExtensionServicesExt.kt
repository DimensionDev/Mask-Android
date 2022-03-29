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

import com.dimension.maskbook.extension.export.ExtensionServices

suspend inline fun <reified T : Any> ExtensionServices.execute(method: String, vararg args: Pair<String, Any>): T? {
    val isWait = T::class != Unit::class
    val result = runJSMethod(method, isWait, *args) ?: return null
    return when (T::class) {
        Short::class -> result.toShortOrNull() as T?
        Int::class -> result.toIntOrNull() as T?
        Long::class -> result.toLongOrNull() as T?
        Float::class -> result.toFloatOrNull() as T?
        Double::class -> result.toDoubleOrNull() as T?
        Boolean::class -> result.toBooleanStrictOrNull() as T?
        String::class -> result as T?
        else -> result.decodeJson() as T?
    }
}
