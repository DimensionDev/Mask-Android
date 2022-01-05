package com.dimension.maskbook.debankapi.api

import com.dimension.maskbook.debankapi.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface TokenResources {
    @GET("/v1/token")
    suspend fun token(
        @Query("id") id: String,
        @Query("chain_id") chainId: ChainID
    ): Token

    @GET("/v1/token/list_by_ids")
    suspend fun listById(
        @Query("chain_id") chainId: ChainID,
        @Query("ids") id: List<String>,
    )
}