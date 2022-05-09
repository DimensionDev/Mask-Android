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

import com.dimension.maskbook.wallet.data.Web3Request
import com.dimension.maskbook.wallet.export.model.ChainType
import kotlinx.coroutines.flow.Flow

interface WalletConnectServerManager {
    val connectedClients: Flow<List<WCClientMeta>>
    fun connectClient(wcUri: String, onRequest: (clientMeta: WCClientMeta) -> Unit)
    fun approveConnect(clientMeta: WCClientMeta, accounts: List<String>, chainId: Long)
    fun rejectConnect(clientMeta: WCClientMeta)
    fun approveRequest(clientMeta: WCClientMeta, requestId: Long, response: Any)
    fun rejectRequest(clientMeta: WCClientMeta, requestId: Long, errorCode: Long, errorMessage: String)
    fun init(onRequest: (clientMeta: WCClientMeta, request: Web3Request) -> Unit)
}

data class WCClientMeta(
    val id: String,
    val name: String,
    val url: String,
    val description: String,
    val icons: List<String>,
    val accounts: List<String>,
    val chainType: ChainType,
)
