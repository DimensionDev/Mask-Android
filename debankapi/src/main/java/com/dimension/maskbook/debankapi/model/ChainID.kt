package com.dimension.maskbook.debankapi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ChainID {
    @SerialName("eth") eth,
    @SerialName("bsc") bsc,
    @SerialName("xdai") xdai,
    @SerialName("matic") matic,
    @SerialName("ftm") ftm,
    @SerialName("okt") okt,
    @SerialName("heco") heco,
    @SerialName("avax") avax,
    @SerialName("op") op,
    @SerialName("arb") arb,
    @SerialName("celo") celo,
}