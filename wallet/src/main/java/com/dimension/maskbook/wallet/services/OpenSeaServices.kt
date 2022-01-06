package com.dimension.maskbook.wallet.services

import com.dimension.maskbook.wallet.services.model.OpenSeaAssetModel
import retrofit2.http.GET

interface OpenSeaServices {
    @GET("/api/v1/assets")
    suspend fun assets(
        owner: String,
        order_direction: String = "desc",
        offset: Int = 0,
        limit: Int = 50,
    ): OpenSeaAssetModel
}