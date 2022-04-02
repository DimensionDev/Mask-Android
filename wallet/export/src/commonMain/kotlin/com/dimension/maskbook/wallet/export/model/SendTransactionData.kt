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
package com.dimension.maskbook.wallet.export.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendTransactionData(
    val from: String? = null,
    val to: String? = null,
    val value: String? = null,
    val gas: String? = null,
    val gasPrice: String? = null,
    @SerialName("maxFeePerGas")
    val maxFee: String? = null,
    @SerialName("maxPriorityFeePerGas")
    val maxPriorityFee: String? = null,
    val data: String? = null,
    val nonce: Long? = null,
    val chainId: Long? = null,
    val common: SendTransactionDataCommon? = null,
    val chain: String? = null,
    val hardfork: String? = null,
)

@Serializable
data class SendTransactionDataCommon(
    val customChain: CustomChainParams?,
    val baseChain: String?,
    val hardfork: String?,
)

@Serializable
data class CustomChainParams(
    val name: String?,
    val networkId: Long?,
    val chainId: Long?,
)
