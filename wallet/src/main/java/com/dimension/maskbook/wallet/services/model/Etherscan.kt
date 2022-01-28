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
package com.dimension.maskbook.wallet.services.model

import kotlinx.serialization.Serializable

@Serializable
data class EtherscanResponse<T>(
    val status: String? = null,
    val message: String? = null,
    val result: List<T>? = null
)

@Serializable
data class EtherscanAssetResult(
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
