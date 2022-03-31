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
package com.dimension.maskbook.labs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RedPacketOptions(
    @SerialName("redpacketPayload")
    val payload: RedPacketJSONPayload,
    val availability: RedPacketAvailability,
    val postLink: String,
) {
    @Serializable
    data class RedPacketJSONPayload(
        // Basic
        @SerialName("contract_address")
        val contractAddress: String,
        @SerialName("rpid")
        val rpId: String,
        @SerialName("txid")
        val txId: String,
        val password: String,
        val shares: Int,
        @SerialName("is_random")
        val isRandom: Boolean,
        val total: String,
        @SerialName("creation_time")
        val creationTime: Long = 0,
        val duration: Int = 0,
        @SerialName("block_number")
        val blockNumber: Int? = null,

        // Payload
        val sender: Sender,
        @SerialName("contract_version")
        val contractVersion: Int,
        val network: String,
        val token: ERC20Token? = null,
        val tokenAddress: String? = null,
        val tokenType: Int? = null,
        val claimers: List<Claimer>,
        @SerialName("total_remaining")
        val totalRemaining: String,
    ) {
        @Serializable
        data class Sender(
            val address: String,
            val name: String,
            val message: String,
        )

        @Serializable
        data class Claimer(
            val address: String,
            val name: String,
        )

        @Serializable
        data class ERC20Token(
            val symbol: String,
            val address: String,
            val decimals: Int,
            val logoURI: String,
            val type: Int,
            val chainId: Int,
            val name: String,
        )
    }

    @Serializable
    data class RedPacketAvailability(
        @SerialName("token_address")
        val tokenAddress: String,
        val balance: String,
        val total: String,
        val claimed: String,
        val expired: Boolean,
        @SerialName("claimed_amount")
        val claimedAmount: String? = null, // V2
        @SerialName("ifclaimed")
        val ifClaimed: String? = null, // V1
    )
}
