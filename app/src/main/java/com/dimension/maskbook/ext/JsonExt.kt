package com.dimension.maskbook.ext

import kotlinx.serialization.json.*

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