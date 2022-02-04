package com.dimension.maskbook.common.retrofit

import com.dimension.maskbook.common.okhttp.okHttpClient
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

val JSON by lazy {
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
}

inline fun <reified T> retrofit(
    baseUrl: String,
): T {
    return Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(JSON.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(T::class.java)
}