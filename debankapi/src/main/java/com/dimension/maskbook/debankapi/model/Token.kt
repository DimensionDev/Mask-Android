package com.dimension.maskbook.debankapi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token (
    val id: String? = null,
    val chain: String? = null,
    val name: String? = null,
    val symbol: String? = null,

    @SerialName("display_symbol")
    val displaySymbol: String? = null,

    @SerialName("optimized_symbol")
    val optimizedSymbol: String? = null,

    val decimals: Long? = null,

    @SerialName("logo_url")
    val logoURL: String? = null,

    @SerialName("protocol_id")
    val protocolID: String? = null,

    val price: Double? = null,

    @SerialName("is_verified")
    val isVerified: Boolean? = null,

    @SerialName("is_core")
    val isCore: Boolean? = null,

    @SerialName("is_wallet")
    val isWallet: Boolean? = null,

    @SerialName("time_at")
    val timeAt: Float? = null,

    val amount: Double? = null,

    val category: String? = null,

    @SerialName("is_collateral")
    val isCollateral: Boolean? = null,

    @SerialName("daily_yield_rate")
    val dailyYieldRate: Double? = null,

    @SerialName("borrow_rate")
    val borrowRate: Double? = null,

    @SerialName("claimable_amount")
    val claimableAmount: Double? = null
)