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
package com.dimension.maskbook.wallet.services

import android.util.Log
import com.dimension.maskbook.debankapi.api.DebankResources
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.ext.JSON
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

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
    val openSeaServices by lazy {
        retrofit<OpenSeaServices>("https://api.opensea.io")
    }
    val etherscanServices by lazy {
        retrofit<EtherscanServices>("https://api.etherscan.io")
    }

    val walletConnectServices by lazy {
        retrofit<WalletConnectServices>("https://registry.walletconnect.org")
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
