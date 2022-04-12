/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.debankapi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chain(
    val id: String? = null,

    @SerialName("community_id")
    val communityID: Long? = null,

    val name: String? = null,

    @SerialName("native_token_id")
    val nativeTokenID: String? = null,

    @SerialName("logo_url")
    val logoURL: String? = null,

    @SerialName("wrapped_token_id")
    val wrappedTokenID: String? = null,
    @SerialName("usd_value")
    val usdValue: Float? = null
)

@Serializable
data class Transaction(
    @SerialName("_cache_seconds")
    val cacheSeconds: Long? = null,

    @SerialName("_seconds")
    val seconds: Double? = null,

    val data: Data? = null,

    @SerialName("error_code")
    val errorCode: Long? = null
)

@Serializable
data class Data(
    @SerialName("cate_dict")
    val cateDict: CateDict? = null,

    @SerialName("history_list")
    val historyList: List<HistoryList>? = null,

    @SerialName("project_dict")
    val projectDict: Map<String, ProjectDict>? = null,

    @SerialName("token_dict")
    val tokenDict: Map<String, TokenDict>? = null
)

@Serializable
data class CateDict(
    val approve: Approve? = null,
    val cancel: Approve? = null,
    val receive: Approve? = null,
    val send: Approve? = null
)

@Serializable
data class Approve(
    val ch: String? = null,
    val en: String? = null,
    val id: String? = null
)

@Serializable
data class HistoryList(
    @SerialName("cate_id")
    val cateID: String? = null,

//    @SerialName("debt_liquidated")
//    val debtLiquidated: Any? = null,

    val id: String? = null,

    @SerialName("other_addr")
    val otherAddr: String? = null,

    @SerialName("project_id")
    val projectID: String? = null,

    val receives: List<Receive>? = null,
    val sends: List<Send>? = null,

    @SerialName("time_at")
    val timeAt: Double? = null,

    @SerialName("token_approve")
    val tokenApprove: TokenApprove? = null,

    val tx: Tx? = null
)

@Serializable
data class Receive(
    val amount: Double? = null,

    @SerialName("from_addr")
    val fromAddr: String? = null,

    @SerialName("token_id")
    val tokenID: String? = null
)

@Serializable
data class Send(
    val amount: Double? = null,

    @SerialName("to_addr")
    val toAddr: String? = null,

    @SerialName("token_id")
    val tokenID: String? = null
)

@Serializable
data class TokenApprove(
    val spender: String? = null,

    @SerialName("token_id")
    val tokenID: String? = null,

    val value: Double? = null
)

@Serializable
data class Tx(
    @SerialName("eth_gas_fee")
    val ethGasFee: Double? = null,

    @SerialName("from_addr")
    val fromAddr: String? = null,

    val name: String? = null,
    val status: Long? = null,

    @SerialName("to_addr")
    val toAddr: String? = null,

    @SerialName("usd_gas_fee")
    val usdGasFee: Double? = null,

    val value: Double? = null
)

@Serializable
data class ProjectDict(
    val id: String? = null,

    @SerialName("logo_url")
    val logoURL: String? = null,

    val name: String? = null
)

@Serializable
data class Name(
    val ch: String? = null,
    val en: String? = null
)

@Serializable
data class TokenDict(
    val chain: ChainID? = null,
    val decimals: Long? = null,

    @SerialName("display_symbol")
    val displaySymbol: String? = null,

    val id: String? = null,

    @SerialName("is_core")
    val isCore: Boolean? = null,

    @SerialName("is_verified")
    val isVerified: Boolean? = null,

    @SerialName("is_wallet")
    val isWallet: Boolean? = null,

    @SerialName("logo_url")
    val logoURL: String? = null,

    val name: String? = null,

    @SerialName("optimized_symbol")
    val optimizedSymbol: String? = null,

    val price: Double? = null,

    @SerialName("protocol_id")
    val protocolID: String? = null,

    val symbol: String? = null,

    @SerialName("contract_id")
    val contractId: String? = null,

    @SerialName("inner_id")
    val innerId: String? = null,

    @SerialName("time_at")
    val timeAt: Double? = null
)
