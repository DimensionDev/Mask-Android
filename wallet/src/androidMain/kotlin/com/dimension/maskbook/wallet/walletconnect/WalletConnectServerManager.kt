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

import kotlinx.coroutines.flow.Flow

interface WalletConnectServerManager {
    val connectedClients: Flow<List<WCClientMeta>>
    fun connectClient(wcUri: String, onRequest: (clientMeta: WCClientMeta) -> Unit)
    fun approveConnect(clientMeta: WCClientMeta, accounts: List<String>, chainId: Long)
    fun rejectConnect(clientMeta: WCClientMeta)
    fun approveRequest(clientMeta: WCClientMeta, requestId: String, response: Any)
    fun rejectRequest(clientMeta: WCClientMeta, requestId: String, errorCode: Long, errorMessage: String)
    fun init(onRequest: (clientMeta: WCClientMeta, request: WCRequest) -> Unit)
}

data class WCClientMeta(
    val id: String,
    val name: String,
    val url: String,
    val description: String,
    val icons: List<String>
)

data class WCRequest(
    val id: String,
    val params: WCRequestParams
)

sealed class WCRequestParams {
    data class WCTransaction(
        val from: String,
        val to: String?,
        val nonce: String?,
        val gasPrice: String?,
        val gasLimit: String?,
        val value: String,
        val data: String
    ) : WCRequestParams()

    data class WCSign(
        val address: String,
        val message: String,
    ) : WCRequestParams()
}
