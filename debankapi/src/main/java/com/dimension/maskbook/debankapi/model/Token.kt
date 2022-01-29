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
data class Token(
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
