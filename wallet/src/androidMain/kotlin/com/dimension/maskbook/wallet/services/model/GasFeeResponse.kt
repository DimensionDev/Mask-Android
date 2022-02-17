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
package com.dimension.maskbook.wallet.services.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GasFeeResponse(
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

@Serializable
data class EthGasFeeResponse(
    val low: EthGasFee? = null,
    val medium: EthGasFee? = null,
    val high: EthGasFee? = null,
    val estimatedBaseFee: String? = null,
    val networkCongestion: Double? = null
)

@Serializable
data class EthGasFee(
    val suggestedMaxPriorityFeePerGas: String? = null,
    val suggestedMaxFeePerGas: String? = null,
    val minWaitTimeEstimate: Long? = null,
    val maxWaitTimeEstimate: Long? = null
)

@Serializable
data class MaticGasFeeResponse(
    val safeLow: Double? = null,
    val standard: Double? = null,
    val fast: Double? = null,
    val fastest: Double? = null,
    val blockTime: Long? = null,
    val blockNumber: Long? = null
)
