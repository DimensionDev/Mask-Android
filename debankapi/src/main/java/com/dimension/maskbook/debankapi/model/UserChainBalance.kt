package com.dimension.maskbook.debankapi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserChainBalance (
    @SerialName("usd_value")
    val usdValue: Double? = null
)