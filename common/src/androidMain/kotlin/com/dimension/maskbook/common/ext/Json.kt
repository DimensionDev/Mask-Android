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

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.double
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.long

val JSON by lazy {
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
}

inline fun <reified T> T.encodeJson(): String = JSON.encodeToString(this)

inline fun <reified T, reified R : JsonElement> T.encodeJsonElement(): R =
    JSON.encodeToJsonElement(this) as R

inline fun <reified T> String.decodeJson(): T = JSON.decodeFromString(this)

inline fun <reified T> JsonElement.decodeJson(): T = JSON.decodeFromJsonElement(this)

inline fun <reified T> JsonElement.decodeJson(): T {
    return JSON.decodeFromJsonElement(this)
}

private val LONG_REGEX = Regex("""^-?\d+$""")
private val DOUBLE_REGEX = Regex("""^-?\d+\.\d+(?:E-?\d+)?$""")

val JsonPrimitive.valueOrNull: Any?
    get() = when {
        this is JsonNull -> null
        this.isString -> this.content
        else -> this.content.toBooleanStrictOrNull()
            ?: when {
                LONG_REGEX.matches(this.content) -> this.long
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

fun Any?.toJsonElement(): JsonElement {
    return when (this) {
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Array<*> -> this.toJsonArray()
        is List<*> -> this.toJsonArray()
        is Map<*, *> -> this.toJsonObject()
        is JsonElement -> this
        else -> JsonNull
    }
}

fun Array<*>.toJsonArray(): JsonArray {
    val array = mutableListOf<JsonElement>()
    this.forEach { array.add(it.toJsonElement()) }
    return JsonArray(array)
}

fun List<*>.toJsonArray(): JsonArray {
    val array = mutableListOf<JsonElement>()
    this.forEach { array.add(it.toJsonElement()) }
    return JsonArray(array)
}

fun Map<*, *>.toJsonObject(): JsonObject {
    val map = mutableMapOf<String, JsonElement>()
    this.forEach {
        if (it.key is String) {
            map[it.key as String] = it.value.toJsonElement()
        }
    }
    return JsonObject(map)
}
