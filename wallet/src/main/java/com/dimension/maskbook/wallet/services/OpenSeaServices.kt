package com.dimension.maskbook.wallet.services

import com.dimension.maskbook.wallet.services.model.OpenSeaAssetModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OpenSeaServices {
    @GET("/api/v1/assets")
    suspend fun assets(
        @Query("owner") owner: String,
        @Query("order_direction") order_direction: String = "desc",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 50,
        @Header("X-API-KEY") apiKey: String = ""
    ): OpenSeaAssetModel
}