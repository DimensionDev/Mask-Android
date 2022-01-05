package com.dimension.maskbook.debankapi.api

import com.dimension.maskbook.debankapi.model.ChainID
import com.dimension.maskbook.debankapi.model.Transaction
import retrofit2.http.GET
import retrofit2.http.Query

interface TransactionResources {
    @GET("https://api.debank.com/history/list")
    suspend fun history(
        @Query("chain") chainId: ChainID,
        @Query("user_addr") address: String
    ): Transaction
}