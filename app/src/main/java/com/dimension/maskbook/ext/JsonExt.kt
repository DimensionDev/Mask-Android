/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.ext

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

private val INT_REGEX = Regex("""^-?\d+$""")
private val DOUBLE_REGEX = Regex("""^-?\d+\.\d+(?:E-?\d+)?$""")

val JsonPrimitive.valueOrNull: Any?
    get() = when {
        this is JsonNull -> null
        this.isString -> this.content
        else -> this.content.toBooleanStrictOrNull()
            ?: when {
                INT_REGEX.matches(this.content) -> this.int
                DOUBLE_REGEX.matches(this.content) -> this.double
                else -> throw IllegalArgumentException("Unknown type for JSON value: ${this.content}")
            }
    }

val JsonElement.normalized: Any?
    get() = when (this) {
        is JsonPrimitive -> this.valueOrNull
        is JsonObject -> this.jsonObject.map {
            it.key to it.value.normalized
        }.toMap()
        is JsonArray -> this.jsonArray.map { it.normalized }
        is JsonNull -> null
        else -> this.toString()
    }
