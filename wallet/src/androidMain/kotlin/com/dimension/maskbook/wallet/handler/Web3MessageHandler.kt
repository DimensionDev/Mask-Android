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
package com.dimension.maskbook.wallet.handler

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.common.ext.httpService
import com.dimension.maskbook.common.ext.normalized
import com.dimension.maskbook.common.route.Navigator
import com.dimension.maskbook.wallet.data.Web3Request
import com.dimension.maskbook.wallet.export.model.SendTransactionData
import com.dimension.maskbook.wallet.model.SendTokenRequest
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.route.WalletRoute
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.web3j.protocol.core.Request
import org.web3j.protocol.core.Response

class Web3MessageHandler(
    private val walletRepository: IWalletRepository,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val waitForResponse = mutableMapOf<String, Web3Request>()
    fun handle(request: Web3Request) {
        scope.launch {
            val payload = request.payload
            if (payload?.id != null) {
                when (payload.method) {
                    "getRPCurl" -> {
                        walletRepository.dWebData.firstOrNull()?.chainType?.endpoint?.let {
                            request.message.response(
                                Web3SendResponse.success(request, mapOf("rpcURL" to it))
                            )
                        }
                    }
                    "eth_coinbase" -> {
                        val address = walletRepository.currentWallet.firstOrNull()?.address ?: ""
                        request.message.response(
                            Web3SendResponse.success(
                                request,
                                mapOf("coinbase" to address)
                            )
                        )
                    }
                    "eth_getAccounts", "eth_accounts" -> {
                        val address = walletRepository.currentWallet.firstOrNull()?.address
                        request.message.response(
                            Web3SendResponse.success(
                                request,
                                listOfNotNull(address)
                            )
                        )
                    }
                    "eth_sendTransaction" -> {
                        val dataRaw = payload.params.firstOrNull()
                            ?.decodeJson<SendTransactionData>()
                            ?.encodeJson() ?: return@launch
                        val requestRaw = SendTokenRequest(
                            messageId = request.id,
                            payloadId = request.payload.id,
                            jsonrpc = request.payload.jsonrpc,
                        ).encodeJson()
                        waitForResponse[request.id.toString()] = request
                        Navigator.navigate(WalletRoute.SendTokenConfirm(dataRaw, false, requestRaw))
                    }
                    "personal_sign" -> {
                        val message = payload.params.getOrNull(0)?.normalized as? String
                            ?: return@launch
                        val fromAddress = payload.params.getOrNull(1)?.normalized as? String
                            ?: return@launch
                        val hex = walletRepository.signMessage(message, fromAddress)
                            ?: return@launch
                        request.message.response(
                            Web3SendResponse.success(
                                request,
                                listOfNotNull(hex)
                            )
                        )
                    }
                    else -> {
                        val method = payload.method
                        val chainType =
                            walletRepository.dWebData.firstOrNull()?.chainType ?: return@launch
                        val service = chainType.httpService
                        try {
                            val response = service.send(
                                Request(
                                    method,
                                    payload.params.map { it.normalized },
                                    service,
                                    JsonResponse::class.java
                                ),
                                JsonResponse::class.java
                            )
                            val result = response?.result
                            if (result == null) {
                                request.message.response(
                                    Web3SendResponse.error(
                                        request,
                                        "No response"
                                    )
                                )
                            } else {
                                request.message.response(
                                    Web3SendResponse.success(
                                        request,
                                        when (result) {
                                            is NullNode -> null
                                            is ObjectNode -> ObjectMapper().convertValue(
                                                result,
                                                object : TypeReference<Map<String, Any>>() {},
                                            )
                                            is ArrayNode -> ObjectMapper().convertValue(
                                                result,
                                                object : TypeReference<List<Any>>() {},
                                            )
                                            else -> result.asText()
                                        }
                                    )
                                )
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            request.message.response(
                                Web3SendResponse.error(
                                    request,
                                    e.message ?: "error"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun onResponseSuccess(id: String, result: Any?) {
        waitForResponse.remove(id)?.let {
            it.message.response(
                Web3SendResponse.success(
                    it,
                    result
                )
            )
        }
    }

    fun onResponseError(id: String, error: String) {
        waitForResponse.remove(id)?.let {
            it.message.response(
                Web3SendResponse.error(
                    it,
                    error
                )
            )
        }
    }
}

private inline fun <reified T : Any> Web3SendResponse.Companion.success(request: Web3Request, result: T?): Map<String, Any?> {
    requireNotNull(request.payload)
    return success(
        messageId = request.id,
        jsonrpc = request.payload.jsonrpc,
        payloadId = request.payload.id,
        result = result
    )
}

private fun Web3SendResponse.Companion.error(request: Web3Request, error: String): Map<String, Any?> {
    requireNotNull(request.payload)
    return error(
        messageId = request.id,
        jsonrpc = request.payload.jsonrpc,
        payloadId = request.payload.id,
        error = error
    )
}

class JsonResponse : Response<JsonNode>()
