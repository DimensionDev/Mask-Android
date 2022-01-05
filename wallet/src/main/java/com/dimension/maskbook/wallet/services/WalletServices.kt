package com.dimension.maskbook.wallet.services

import android.util.Log
import com.dimension.maskbook.debankapi.api.DebankResources
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.ext.JSON
import com.dimension.maskbook.wallet.services.model.GasFeeResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@Serializable
enum class AccountType {
    email,
    phone,
}

@Serializable
enum class Scenario {
    backup,
    create_binding,
    change_binding
}

@Serializable
enum class Locale {
    en,
    zh
}

@Serializable
data class SendCodeBody(
    val account_type: AccountType,
    val account: String,
    val scenario: Scenario,
    val locale: Locale,
)

@Serializable
data class ValidateCodeBody(
    val code: String,
    val account_type: AccountType,
    val account: String,
)

@Serializable
data class UploadBody(
    val code: String,
    val account_type: AccountType,
    val account: String,
    val abstract: String,
)

@Serializable
data class UploadResponse(
    val upload_url: String?,
)

@Serializable
data class DownloadResponse(
    val download_url: String?,
    val size: Long?,
    val uploaded_at: Long?,
    val abstract: String?,
)

@Serializable
data class EthGasFeeResponse(
    val low: EthGasFee? = null,
    val medium: EthGasFee? = null,
    val high: EthGasFee? = null,
    val estimatedBaseFee: String? = null,
    val networkCongestion: Double? = null
)

@Serializable
data class EthGasFee(
    val suggestedMaxPriorityFeePerGas: String? = null,
    val suggestedMaxFeePerGas: String? = null,
    val minWaitTimeEstimate: Long? = null,
    val maxWaitTimeEstimate: Long? = null
)

@Serializable
data class MaticGasFeeResponse(
    val safeLow: Double? = null,
    val standard: Double? = null,
    val fast: Double? = null,
    val fastest: Double? = null,
    val blockTime: Long? = null,
    val blockNumber: Long? = null
)

interface BackupServices {
    @POST("/api/v1/backup/send_code")
    suspend fun sendCode(
        @Body
        body: SendCodeBody,
    )

    @POST("/api/v1/backup/validate_code")
    suspend fun validateCode(
        @Body
        body: ValidateCodeBody,
    )

    @POST("/api/v1/backup/upload")
    suspend fun upload(
        @Body
        body: UploadBody,
    ): UploadResponse

    @POST("/api/v1/backup/download")
    suspend fun download(
        @Body
        body: ValidateCodeBody,
    ): DownloadResponse
}

interface GasServices {
    @GET("https://ethgasstation.info/api/ethgasAPI.json")
    suspend fun ethGas(): GasFeeResponse

    @GET("https://gas-api.metaswap.codefi.network/networks/1/suggestedGasFees")
    suspend fun ethGasFee(): EthGasFeeResponse

    @GET("https://gasstation-mainnet.matic.network")
    suspend fun maticGasFee(): MaticGasFeeResponse
}

class WalletServices {
    val backupServices by lazy {
        retrofit<BackupServices>("https://vaalh28dbi.execute-api.ap-east-1.amazonaws.com")
    }
    val debankServices by lazy {
        retrofit<DebankResources>("https://openapi.debank.com")
    }
    val gasServices by lazy {
        retrofit<GasServices>("https://ethgasstation.info")
    }
}

private inline fun <reified T> retrofit(
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