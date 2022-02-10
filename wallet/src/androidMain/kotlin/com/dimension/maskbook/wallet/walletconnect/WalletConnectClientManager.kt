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
package com.dimension.maskbook.wallet.walletconnect

import com.dimension.maskbook.wallet.repository.ChainType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

// wallets that supported wallet connection
// https://registry.walletconnect.org/data/wallets.json

interface WalletConnectClientManager {
    val wcUrl: Flow<String>
    fun connect(onResult: (success: Boolean, responder: WCResponder?) -> Unit)
    fun disConnect(address: String): Boolean
    fun initSessions(onDisconnect: (address: String) -> Unit)
    fun sendToken(
        amount: BigDecimal, // ether
        fromAddress: String,
        toAddress: String,
        data: String,
        gasLimit: Double,
        gasPrice: BigDecimal, // ether
        onResponse: (response: Any, error: Throwable?) -> Unit
    )
}

data class WCResponder(
    val accounts: List<String>,
    val name: String,
    val description: String,
    val icons: List<String>,
    val url: String,
    val chainType: ChainType,
)

class WCError(val errorCode: String, message: String) : Throwable(message = message)
