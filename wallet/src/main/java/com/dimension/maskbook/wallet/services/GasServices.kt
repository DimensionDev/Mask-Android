package com.dimension.maskbook.wallet.services

import com.dimension.maskbook.wallet.services.model.EthGasFeeResponse
import com.dimension.maskbook.wallet.services.model.GasFeeResponse
import com.dimension.maskbook.wallet.services.model.MaticGasFeeResponse
import retrofit2.http.GET

interface GasServices {
    @GET("https://ethgasstation.info/api/ethgasAPI.json")
    suspend fun ethGas(): GasFeeResponse

    @GET("https://gas-api.metaswap.codefi.network/networks/1/suggestedGasFees")
    suspend fun ethGasFee(): EthGasFeeResponse

    @GET("https://gasstation-mainnet.matic.network")
    suspend fun maticGasFee(): MaticGasFeeResponse
}