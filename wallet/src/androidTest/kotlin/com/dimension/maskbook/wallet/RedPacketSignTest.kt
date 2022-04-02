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
package com.dimension.maskbook.wallet

import com.dimension.maskbook.wallet.util.SignUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class RedPacketSignTest {

    @Test
    fun test() {
        val message = "0x790116d0685eB197B886DAcAD9C247f785987A4a"
        val password = "0x2e0c577c80bd39cb10815c9fec98f70c081d75647c15118da9a93e3970d4860d"
        val sign = SignUtils.signMessage(message, password)
        assertEquals(
            sign,
            "0xbfd98d17fab451480902d52adcc0adbb80e4408bf26e4bfe10906ea1d84495e146670ff0ed90c4ab7c8093ca1238f6115795a7f5fad26f2ce441160ddd797a691b"
        )
    }
}
