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
package com.dimension.maskbook.debankapi.api

import com.dimension.maskbook.debankapi.model.ChainID
import com.dimension.maskbook.debankapi.model.NFTElement
import com.dimension.maskbook.debankapi.model.Protocol
import com.dimension.maskbook.debankapi.model.ProtocolID
import com.dimension.maskbook.debankapi.model.Token
import com.dimension.maskbook.debankapi.model.UserChainBalance
import com.dimension.maskbook.debankapi.model.UserTotalBalance
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
