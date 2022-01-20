package com.dimension.maskbook.wallet.services

import com.dimension.maskbook.wallet.services.model.EtherscanAssetResult
import com.dimension.maskbook.wallet.services.model.EtherscanResponse
import retrofit2.http.GET

interface EtherscanServices {
    @GET("/api")
    suspend fun assetEvent(
        contractaddress: String,
        address: String,
        module: String = "account",
        action: String = "tokennfttx",
        apikey: String = "",
        page: Int = 1,
        offset: Int = 100,
    ): EtherscanResponse<EtherscanAssetResult>
}