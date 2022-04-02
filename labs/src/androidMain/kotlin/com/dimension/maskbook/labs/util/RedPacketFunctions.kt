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

import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.utils.Numeric

object RedPacketFunctions {

    fun claim(rpId: String, signedMsg: String, address: String): String {
        return Function(
            "claim",
            listOf(
                Bytes32(Numeric.hexStringToByteArray(rpId)),
                DynamicBytes(Numeric.hexStringToByteArray(signedMsg)),
                Address(address),
            ),
            listOf()
        ).let {
            FunctionEncoder.encode(it)
        }
    }

    fun refund(rpId: String): String {
        return Function(
            "refund",
            listOf(
                Bytes32(Numeric.hexStringToByteArray(rpId)),
            ),
            listOf()
        ).let {
            FunctionEncoder.encode(it)
        }
    }
}
