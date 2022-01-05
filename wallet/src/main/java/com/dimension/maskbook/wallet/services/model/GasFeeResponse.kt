package com.dimension.maskbook.wallet.services.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GasFeeResponse (
    val fast: Double? = null,
    val fastest: Double? = null,
    val safeLow: Double? = null,
    val average: Double? = null,

    @SerialName("block_time")
    val blockTime: Double? = null,

    val blockNum: Double? = null,
    val speed: Double? = null,
    val safeLowWait: Double? = null,
    val avgWait: Double? = null,
    val fastWait: Double? = null,
    val fastestWait: Double? = null,
    val gasPriceRange: Map<String, Double>? = null
)
