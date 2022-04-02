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

import kotlin.test.Test
import kotlin.test.assertEquals

class RedPacketFunctionsTest {
    @Test
    fun test_claim() {
        val rpId = "0x28c753f8a88a0d5e80dc3bf15e78d5449d1cd4ef273cfeda5fd2fe786929c708"
        val messageSign = "0xbfd98d17fab451480902d52adcc0adbb80e4408bf26e4bfe10906ea1d84495e146670ff0ed90c4ab7c8093ca1238f6115795a7f5fad26f2ce441160ddd797a691b"
        val address = "0x790116d0685eB197B886DAcAD9C247f785987A4a"
        val data = RedPacketFunctions.claim(rpId, messageSign, address)
        assertEquals(
            data,
            "0x7394ad9328c753f8a88a0d5e80dc3bf15e78d5449d1cd4ef273cfeda5fd2fe786929c7080000000000000000000000000000000000000000000000000000000000000060000000000000000000000000790116d0685eb197b886dacad9c247f785987a4a0000000000000000000000000000000000000000000000000000000000000041bfd98d17fab451480902d52adcc0adbb80e4408bf26e4bfe10906ea1d84495e146670ff0ed90c4ab7c8093ca1238f6115795a7f5fad26f2ce441160ddd797a691b00000000000000000000000000000000000000000000000000000000000000"
        )
    }
}
