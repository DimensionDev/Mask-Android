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
data class NFTElement(
    val id: String? = null,

    @SerialName("contract_id")
    val contractID: String? = null,

    @SerialName("inner_id")
    val innerID: String? = null,

    val chain: Chain? = null,
    val name: String? = null,
    val description: String? = null,

    @SerialName("content_type")
    val contentType: String? = null,

    val content: String? = null,

    @SerialName("total_supply")
    val totalSupply: Long? = null,

    @SerialName("detail_url")
    val detailURL: String? = null,

    @SerialName("contract_name")
    val contractName: String? = null,

    @SerialName("is_erc1155")
    val isErc1155: Boolean? = null,

    val amount: Long? = null,
    val protocol: Protocol? = null,

    @SerialName("pay_token")
    val payToken: Token? = null,

    @SerialName("usd_price")
    val usdPrice: Double? = null,

    @SerialName("is_erc721")
    val isErc721: Boolean? = null
)
