package com.dimension.maskbook.wallet.ext

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val JSON by lazy {
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
}

inline fun <reified T> T.encodeJson(): String =
    JSON.encodeToString(this)

inline fun <reified T> String.decodeJson(): T {
    return JSON.decodeFromString(this)
}
