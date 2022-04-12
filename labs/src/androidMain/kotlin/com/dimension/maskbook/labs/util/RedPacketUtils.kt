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
package com.dimension.maskbook.labs.util

import com.dimension.maskbook.common.util.EthUtils
import com.dimension.maskbook.labs.model.options.RedPacketOptions
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.utils.Numeric
import java.math.BigInteger

object RedPacketUtils {

    object Functions {
        fun claim(rpId: String, signedMsg: String, address: String): Function {
            return Function(
                "claim",
                listOf(
                    Bytes32(Numeric.hexStringToByteArray(rpId)),
                    DynamicBytes(Numeric.hexStringToByteArray(signedMsg)),
                    Address(address),
                ),
                listOf()
            )
        }

        fun refund(rpId: String): Function {
            return Function(
                "refund",
                listOf(
                    Bytes32(Numeric.hexStringToByteArray(rpId)),
                ),
                listOf()
            )
        }

        fun checkAvailability(rpId: String): Function {
            return Function(
                "check_availability",
                listOf(
                    Bytes32(Numeric.hexStringToByteArray(rpId)),
                ),
                listOf(
                    object : TypeReference<Address>() {},
                    object : TypeReference<Uint256>() {},
                    object : TypeReference<Uint256>() {},
                    object : TypeReference<Uint256>() {},
                    object : TypeReference<Bool>() {},
                    object : TypeReference<Uint256>() {},
                )
            )
        }
    }

    fun checkAvailability(
        web3j: Web3j,
        fromAddress: String,
        contractAddress: String,
        rpId: String,
    ): Result<RedPacketOptions.RedPacketAvailability> {
        return EthUtils.ethCall(
            web3j = web3j,
            fromAddress = fromAddress,
            contractAddress = contractAddress,
            function = Functions.checkAvailability(rpId),
        ).map {
            RedPacketOptions.RedPacketAvailability(
                tokenAddress = it.values[0] as String,
                balance = (it.values[1] as BigInteger).toBigDecimal(),
                total = (it.values[2] as BigInteger).toBigDecimal(),
                claimed = (it.values[3] as BigInteger).toBigDecimal(),
                expired = it.values[4] as Boolean,
                claimedAmount = (it.values[5] as BigInteger).toBigDecimal(),
            )
        }
    }
}
