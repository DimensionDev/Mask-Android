package com.dimension.maskbook.debankapi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserTotalBalance (
    @SerialName("total_usd_value")
    val totalUsdValue: Double? = null,

    @SerialName("chain_list")
    val chainList: List<Chain>? = null
)