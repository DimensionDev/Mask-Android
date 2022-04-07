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
package com.dimension.maskbook.labs.model.options

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.decodeJson
import kotlin.test.Test
import kotlin.test.assertEquals

class RedPacketOptionsTest {

    private val redPacketJson = """
        {
            "redpacketPayload":
            {
                "rpid": "0x..",
                "is_random": false,
                "sender":
                {
                    "address": "0x..",
                    "name": "aaaa",
                    "message": "Best Wishes!"
                },
                "claimers": [],
                "block_number": 1111,
                "txid": "0x..",
                "contract_address": "0x..",
                "total": "2500000000000000",
                "total_remaining": "2500000000000000",
                "password": "0x..",
                "token":
                {
                    "symbol": "MATIC",
                    "address": "0x0000000000000000000000000000000000000000",
                    "decimals": 18,
                    "logoURI": "https://static.debank.com/image/matic_token/logo_url/matic/e5a8a2860ba5cf740a474dcab796dc63.png",
                    "type": 0,
                    "chainId": 137,
                    "name": "Matic"
                },
                "contract_version": 4,
                "duration": 86400000,
                "network": "Matic Mainnet",
                "shares": 5,
                "token_address": "0x0000000000000000000000000000000000000000",
                "creation_time": 1648618347000
            },
            "availability":
            {
                "token_address": "0x0000000000000000000000000000000000000000",
                "balance": "2500000000000000",
                "total": "5",
                "claimed": "0",
                "expired": false,
                "claimed_amount": "0"
            },
            "postLink": "https://twitter.com/..."
        }
    """.trimIndent()

    @Test
    fun test_RedPacketOptions() {
        val options: RedPacketOptions = redPacketJson.decodeJson()
        assertEquals(options.postLink, "https://twitter.com/...")
        assertEquals(options.availability.total, BigDecimal(5))
    }
}
