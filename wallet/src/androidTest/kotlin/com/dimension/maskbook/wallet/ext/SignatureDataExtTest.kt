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
package com.dimension.maskbook.wallet.ext

import org.web3j.crypto.Sign
import org.web3j.utils.Numeric
import kotlin.test.Test
import kotlin.test.assertEquals

class SignatureDataExtTest {
    @Test
    fun test_signature() {
        val sign = Sign.SignatureData(
            Numeric.hexStringToByteArray("0x1c"),
            Numeric.hexStringToByteArray("0xb91467e570a6466aa9e9876cbcd013baba02900b8979d43fe208a4a4f339f5fd"),
            Numeric.hexStringToByteArray("0x6007e74cd82e037b800186422fc2da167c747ef045e5d18a5f5d4300f8e1a029"),
        )
        assertEquals(
            Numeric.toHexString(sign.signature),
            "0xb91467e570a6466aa9e9876cbcd013baba02900b8979d43fe208a4a4f339f5fd6007e74cd82e037b800186422fc2da167c747ef045e5d18a5f5d4300f8e1a0291c"
        )
    }
}
