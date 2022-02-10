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
package com.dimension.maskbook.handler

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.repository.Web3Request
import com.dimension.maskbook.common.util.ExtensionResponseMessage
import com.dimension.maskbook.common.util.MessageChannel
import com.dimension.maskbook.ext.normalized
import com.dimension.maskbook.platform.PlatformSwitcher
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.SendTokenConfirmData
import com.dimension.maskbook.wallet.repository.SendTransactionData
import com.dimension.maskbook.wallet.repository.httpService
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
import org.web3j.crypto.Credentials
import org.web3j.crypto.Sign
import org.web3j.crypto.Sign.SignatureData
import org.web3j.protocol.core.Request
import org.web3j.protocol.core.Response
import org.web3j.utils.Numeric
import java.nio.charset.Charset

class Web3MessageHandler(
    private val walletRepository: IWalletRepository,
    private val platformSwitcher: PlatformSwitcher,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    fun handle(request: Web3Request) {
        scope.launch {
            val payload = request.payload
            if (payload?.id != null) {
                when (payload.method) {
                    "getRPCurl" -> {
                        walletRepository.dWebData.firstOrNull()?.chainType?.endpoint?.let {
                            MessageChannel.sendResponseMessage(
                                ExtensionResponseMessage.success(request, mapOf("rpcURL" to it))
                            )
                        }
                    }
                    "eth_coinbase" -> {
                        val address = walletRepository.currentWallet.firstOrNull()?.address ?: ""
                        MessageChannel.sendResponseMessage(
                            ExtensionResponseMessage.success(
                                request,
                                mapOf("coinbase" to address)
                            )
                        )
                    }
                    "eth_getAccounts", "eth_accounts" -> {
                        val address = walletRepository.currentWallet.firstOrNull()?.address
                        MessageChannel.sendResponseMessage(
                            ExtensionResponseMessage.success(
                                request,
                                listOfNotNull(address)
                            )
                        )
                    }
                    "eth_sendTransaction" -> {
                        val data =
                            payload.params.firstOrNull()?.toString()
                                ?.decodeJson<SendTransactionData>()
                                ?: return@launch
                        platformSwitcher.showModal(
                            "SendTokenConfirm",
                            SendTokenConfirmData(
                                data,
                                request.id,
                                onDone = {
                                    it?.let {
                                        MessageChannel.sendResponseMessage(
                                            ExtensionResponseMessage.success(
                                                request,
                                                it
                                            )
                                        )
                                    } ?: run {
                                        MessageChannel.sendResponseMessage(
                                            ExtensionResponseMessage.error(
                                                request,
                                                "Transaction failed"
                                            )
                                        )
                                    }
                                },
                                onCancel = {
                                    MessageChannel.sendResponseMessage(
                                        ExtensionResponseMessage.error(
                                            request,
                                            "User canceled"
                                        )
                                    )
                                },
                                onError = {
                                    MessageChannel.sendResponseMessage(
                                        ExtensionResponseMessage.error(
                                            request,
                                            it.message ?: "error"
                                        )
                                    )
                                }
                            )
                        )
                    }
                    "personal_sign" -> {
                        val message =
                            payload.params.getOrNull(0)?.normalized as? String
                                ?: return@launch
                        val fromAddress =
                            payload.params.getOrNull(1)?.normalized as? String
                                ?: return@launch
//                        val password = payload.params.getOrNull(2) ?: return@launch
                        val wallet =
                            walletRepository.findWalletByAddress(fromAddress) ?: return@launch
                        val privateKey =
                            walletRepository.getPrivateKey(wallet, CoinPlatformType.Ethereum)
                        val credentials = Credentials.create(privateKey)
                        val data = Sign.signPrefixedMessage(
                            message.toByteArray(Charset.forName("UTF-8")),
                            credentials.ecKeyPair,
                        )
                        val signature = getSignature(data)
                        val hex = Numeric.toHexString(signature)
                        MessageChannel.sendResponseMessage(
                            ExtensionResponseMessage.success(
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
                                MessageChannel.sendResponseMessage(
                                    ExtensionResponseMessage.error(
                                        request,
                                        "No response"
                                    )
                                )
                            } else {
                                MessageChannel.sendResponseMessage(
                                    ExtensionResponseMessage.success(
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
                            MessageChannel.sendResponseMessage(
                                ExtensionResponseMessage.error(
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
}

private fun getSignature(sigData: SignatureData): ByteArray {
    val magic1 = byteArrayOf(0, 27, 31, 35)
    val magic2 = byteArrayOf(1, 28, 32, 36)
    val v = sigData.v.firstOrNull() ?: throw Error("v is empty")
    return sigData.r + sigData.s + when {
        magic1.contains(v) -> {
            0x1b
        }
        magic2.contains(v) -> {
            0x1c
        }
        else -> {
            throw Error("invalid v")
        }
    }
}

class JsonResponse : Response<JsonNode>()
