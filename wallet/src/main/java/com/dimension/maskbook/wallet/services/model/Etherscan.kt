package com.dimension.maskbook.wallet.services.model

import kotlinx.serialization.Serializable

@Serializable
data class EtherscanResponse<T>(
    val status: String? = null,
    val message: String? = null,
    val result: List<T>? = null
)

@Serializable
data class EtherscanAssetResult (
    val blockNumber: String? = null,
    val timeStamp: String? = null,
    val hash: String? = null,
    val nonce: String? = null,
    val blockHash: String? = null,
    val from: String? = null,
    val contractAddress: String? = null,
    val to: String? = null,
    val tokenID: String? = null,
    val tokenName: String? = null,
    val tokenSymbol: String? = null,
    val tokenDecimal: String? = null,
    val transactionIndex: String? = null,
    val gas: String? = null,
    val gasPrice: String? = null,
    val gasUsed: String? = null,
    val cumulativeGasUsed: String? = null,
    val input: String? = null,
    val confirmations: String? = null
)
