package com.dimension.maskbook.debankapi.api

import com.dimension.maskbook.debankapi.model.Chain
import com.dimension.maskbook.debankapi.model.ChainID
import retrofit2.http.GET
import retrofit2.http.Query

interface ChainResources {
    @GET("/v1/chain")
    suspend fun getChainInfo(
        @Query("id") chainId: ChainID
    ): Chain

    @GET("/v1/chain/list")
    suspend fun getChainList(): List<Chain>
}