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
package com.dimension.maskbook.labs.model.ui

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.wallet.export.model.ChainType

data class UiLuckyDropData(
    val wallet: Wallet = Wallet(),
    val redPacket: RedPacket = RedPacket(),
    val buttonEnabled: Boolean = false,
    val buttonStringRes: Int = 0,
) {
    data class Wallet(
        val name: String = "",
        val address: String = "",
        val chainId: Long = 0,
        val chainType: ChainType = ChainType.eth,
        val chainTypeIcon: Int = 0,
        val chainBalance: String = "",
        val decimals: Int = 0,
        val symbol: String = "",
    )

    data class RedPacket(
        val contractAddress: String = "",
        val message: String = "",
        val senderName: String = "",
        val shares: Int = 0,
        val amount: BigDecimal = BigDecimal.ZERO,
        val amountString: String = "",
        val endTime: String = "",
        val stateStringRes: Int = 0,
        val rpId: String = "",
        val password: String = "",
        val canClaim: Boolean = false,
        val canRefund: Boolean = false,
        val canSend: Boolean = false,
        val postLink: String = "",
    )
}
