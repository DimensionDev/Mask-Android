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
package com.dimension.maskbook.wallet.walletconnect.v1.server

import com.dimension.maskbook.common.ext.toJsonArray
import com.dimension.maskbook.extension.export.model.ExtensionId
import com.dimension.maskbook.extension.export.model.ExtensionMessage
import com.dimension.maskbook.wallet.data.JsonRpcPayload
import com.dimension.maskbook.wallet.data.Web3Request
import com.dimension.maskbook.wallet.export.model.SendTransactionData
import kotlinx.serialization.json.JsonArray
import org.walletconnect.Session

fun Session.MethodCall.toWeb3Request(chainId: Long, onResponse: (Map<String, Any?>) -> Unit): Web3Request {
    return Web3Request(
        id = ExtensionId.fromAny(id()),
        payload = JsonRpcPayload(
            jsonrpc = "2.0",
            method = method(),
            params = params(chainId = chainId),
            id = ExtensionId.fromAny(id())
        ),
        message = ExtensionMessage(
            id = ExtensionId.fromAny(id()),
            jsonrpc = "2.0",
            method = method(),
            params = null,
            onResponse = onResponse
        )
    )
}

fun Session.MethodCall.method() = when (this) {
    is Session.MethodCall.SendTransaction -> "eth_sendTransaction"
    is Session.MethodCall.SignMessage -> "eth_sign"
    is Session.MethodCall.Custom -> method
    else -> ""
}

fun Session.MethodCall.params(chainId: Long): JsonArray = when (this) {
    is Session.MethodCall.SendTransaction -> listOf(
        SendTransactionData(
            from = from,
            to = to,
            value = value,
            gasLimit = gasLimit,
            gasPrice = gasPrice,
            nonce = nonce?.toLong(),
            data = data,
            chainId = chainId,
        )
    ).toJsonArray()
    is Session.MethodCall.SignMessage -> listOf(address, message).toJsonArray()
    is Session.MethodCall.Custom -> params?.toJsonArray() ?: emptyList<String>().toJsonArray()
    else -> JsonArray(emptyList())
}
