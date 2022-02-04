package com.dimension.maskbook.common.okhttp

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.dimension.maskbook.common.okhttp.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

val okHttpClient by lazy {
    OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor(HttpLogger()).apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                )
            }
        }
        .build()
}

class HttpLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        Log.i("HttpLogger", message)
    }
}

suspend fun Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isCancelled) return
                continuation.resumeWithException(e)
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Throwable) {
            }
        }
    }
}
