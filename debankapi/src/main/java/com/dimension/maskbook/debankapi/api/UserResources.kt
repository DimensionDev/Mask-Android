package com.dimension.maskbook.debankapi.api

import com.dimension.maskbook.debankapi.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface UserResources {
    @GET("/v1/user/chain_balance")
    suspend fun chainBalance(
        @Query("id") address: String,
        @Query("chain_id") chainId: ChainID
    ): UserChainBalance

    @GET("/v1/user/total_balance")
    suspend fun totalBalance(
        @Query("id") address: String
    ): UserTotalBalance

    @GET("/v1/user/complex_protocol_list")
    suspend fun complexProtocolList(
        @Query("id") address: String,
        @Query("chain_id") chainId: ChainID,
    ): List<Protocol>

    @GET("/v1/user/nft_list")
    suspend fun nftList(
        @Query("id") address: String,
        @Query("chain_id") chainId: ChainID,
    ): List<NFTElement>

    @GET("/v1/user/protocol")
    suspend fun protocol(
        @Query("id") address: String,
        @Query("protocol_id") protocolId: ProtocolID,
    ): List<Protocol>

    @GET("/v1/user/simple_protocol_list")
    suspend fun simpleProtocolList(
        @Query("id") address: String,
        @Query("protocol_id") protocolId: ProtocolID,
    ): List<Protocol>

    @GET("/v1/user/token")
    suspend fun token(
        @Query("id") address: String,
        @Query("chain_id") chainId: ChainID,
        @Query("token_id") tokenId: String,
    ): List<Token>

    @GET("/v1/user/token_authorized_list")
    suspend fun tokenAuthorizedList(
        @Query("id") address: String,
        @Query("chain_id") chainId: ChainID,
    ): List<Token>

    @GET("/v1/user/token_list")
    suspend fun tokenList(
        @Query("id") address: String,
        @Query("chain_id") chainId: ChainID,
        @Query("is_all") is_all: Boolean,
        @Query("has_balance") has_balance: Boolean,
    ): List<Token>

    @GET("/v1/user/token_search")
    suspend fun tokenSearch(
        @Query("id") address: String,
        @Query("chain_id") chainId: ChainID,
        @Query("q") q: String,
        @Query("has_balance") has_balance: Boolean,
    ): List<Token>
}