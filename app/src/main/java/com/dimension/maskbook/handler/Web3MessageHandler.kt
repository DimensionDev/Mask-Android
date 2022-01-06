package com.dimension.maskbook.handler

import com.dimension.maskbook.ext.normalized
import com.dimension.maskbook.platform.PlatformSwitcher
import com.dimension.maskbook.repository.Web3Request
import com.dimension.maskbook.util.ExtensionResponseMessage
import com.dimension.maskbook.util.MessageChannel
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.ext.decodeJson
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
            if (request.payload?.id != null) {
                when (request.payload.method) {
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
                            request.payload.params.firstOrNull()?.toString()
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
                            request.payload.params.getOrNull(0)?.normalized as? String
                                ?: return@launch
                        val fromAddress =
                            request.payload.params.getOrNull(1)?.normalized as? String
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
                        val method = request.payload.method
                        val chainType =
                            walletRepository.dWebData.firstOrNull()?.chainType ?: return@launch
                        val service = chainType.httpService
                        try {
                            val response = service.send(
                                Request(
                                    method,
                                    request.payload.params.map { it.normalized },
                                    service,
                                    JsonResponse::class.java
                                ), JsonResponse::class.java
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