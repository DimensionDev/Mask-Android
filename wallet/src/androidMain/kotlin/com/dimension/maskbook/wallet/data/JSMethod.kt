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
package com.dimension.maskbook.wallet.data

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.execute
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.export.model.ExtensionId
import com.dimension.maskbook.extension.export.model.ExtensionMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class SwitchBlockChainData(
    val coinId: Int? = null,
    val networkId: Long? = null,
)

data class Web3Request(
    val id: ExtensionId,
    val payload: JsonRpcPayload?,
    val message: ExtensionMessage,
)

@Serializable
data class JsonRpcPayload(
    val jsonrpc: String,
    val method: String,
    val params: JsonArray,
    val id: ExtensionId,
)

internal class JSMethod(
    private val extensionServices: ExtensionServices,
) {
    suspend fun updateEthereumChainId(chainId: Long) {
        extensionServices.execute<Unit>(
            "wallet_updateEthereumChainId",
            "chainId" to chainId
        )
    }

    suspend fun updateEthereumAccount(address: String) {
        extensionServices.execute<Unit>(
            "wallet_updateEthereumAccount",
            "account" to address
        )
    }

    fun web3Event(): Flow<Web3Request> {
        return extensionServices.subscribeBackgroundJSEvent("send").map {
            Web3Request(
                it.id,
                it.params?.decodeJson<JsonRpcPayload>(),
                it
            )
        }
    }

    fun switchBlockChain(): Flow<SwitchBlockChainData> {
        return extensionServices.subscribeBackgroundJSEvent("wallet_switchBlockChain")
            .mapNotNull { it.params?.decodeJson<SwitchBlockChainData>() }
    }
}
