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
package com.dimension.maskbook.common.util

import com.dimension.maskbook.common.model.EthResponse
import com.dimension.maskbook.common.model.EthTransactionReceiptResponse
import com.dimension.maskbook.wallet.export.model.ChainType
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.Response
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.tx.RawTransactionManager
import java.math.BigInteger

object EthUtils {

    fun ethEstimateGas(
        web3j: Web3j,
        fromAddress: String,
        contractAddress: String? = null,
        data: String? = null,
    ): Result<BigInteger> {
        val transaction = Transaction.createEthCallTransaction(
            fromAddress, contractAddress, data,
        )
        val response = kotlin.runCatching {
            web3j.ethEstimateGas(transaction).sendAsync().get()
        }.getOrElse {
            return Result.failure(it)
        }

        return response.ifSuccess {
            amountUsed
        }
    }

    fun ethCall(
        web3j: Web3j,
        fromAddress: String,
        contractAddress: String,
        functionName: String,
        inputParams: List<Type<*>>,
        outputParams: List<TypeReference<*>>,
    ): Result<EthResponse> {
        return ethCall(
            web3j = web3j,
            fromAddress = fromAddress,
            contractAddress = contractAddress,
            function = Function(functionName, inputParams, outputParams),
        )
    }

    fun ethCall(
        web3j: Web3j,
        fromAddress: String,
        contractAddress: String,
        function: Function,
    ): Result<EthResponse> {
        val encodedFunction = FunctionEncoder.encode(function)

        val transaction = Transaction.createEthCallTransaction(
            fromAddress, contractAddress, encodedFunction
        )
        val response: EthCall = kotlin.runCatching {
            web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get()
        }.getOrElse {
            return Result.failure(it)
        }

        return response.ifSuccess {
            EthResponse(
                transactionHash = "",
                values = parseValues(function.outputParameters),
            )
        }
    }

    fun ethSendRawTransaction(
        web3j: Web3j,
        privateKey: String,
        chainType: ChainType,
        contractAddress: String,
        maxPriorityFeePerGas: BigInteger,
        maxFeePerGas: BigInteger,
        gasLimit: BigInteger,
        value: BigInteger?,
        constructor: Boolean = false,
        functionName: String = "",
        inputParams: List<Type<*>> = emptyList(),
        outputParams: List<TypeReference<*>> = emptyList(),
    ): Result<EthResponse> {
        val function = Function(functionName, inputParams, outputParams)
        val encodedFunction = FunctionEncoder.encode(function)
        return ethSendRawTransaction(
            web3j = web3j,
            privateKey = privateKey,
            chainType = chainType,
            contractAddress = contractAddress,
            maxPriorityFeePerGas = maxPriorityFeePerGas,
            maxFeePerGas = maxFeePerGas,
            gasLimit = gasLimit,
            value = value,
            data = encodedFunction,
            constructor = constructor,
            outputParams = function.outputParameters,
        )
    }

    fun ethSendRawTransaction(
        web3j: Web3j,
        privateKey: String,
        chainType: ChainType,
        contractAddress: String,
        maxPriorityFeePerGas: BigInteger,
        maxFeePerGas: BigInteger,
        gasLimit: BigInteger,
        value: BigInteger?,
        data: String,
        constructor: Boolean = false,
        outputParams: List<TypeReference<Type<*>>> = emptyList(),
    ): Result<EthResponse> {
        val credentials = Credentials.create(privateKey)
        val transaction = RawTransactionManager(web3j, credentials, chainType.chainId)
        val response: EthSendTransaction = kotlin.runCatching {
            if (chainType.supportEip25519) {
                transaction.sendEIP1559Transaction(
                    chainType.chainId,
                    maxPriorityFeePerGas,
                    maxFeePerGas,
                    gasLimit,
                    contractAddress,
                    data,
                    value,
                    constructor,
                )
            } else {
                transaction.sendTransaction(
                    maxPriorityFeePerGas + maxFeePerGas,
                    gasLimit,
                    contractAddress,
                    data,
                    value,
                    constructor
                )
            }
        }.getOrElse {
            return Result.failure(it)
        }

        return response.ifSuccess {
            EthResponse(
                transactionHash = transactionHash,
                values = parseValues(outputParams),
            )
        }
    }

    fun ethGetTransactionReceipt(
        web3j: Web3j,
        transactionHash: String,
    ): Result<EthTransactionReceiptResponse> {
        val response = kotlin.runCatching {
            web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get()
        }.getOrElse {
            return Result.failure(it)
        }

        return response.ifSuccessResult {
            val transactionReceipt = response.result
                ?: return@ifSuccessResult Result.failure(Exception("null transactionReceipt"))

            Result.success(
                EthTransactionReceiptResponse(
                    transactionHash = transactionReceipt.transactionHash,
                    blockHash = transactionReceipt.blockHash,
                    blockNumber = transactionReceipt.blockNumber,
                    transactionIndex = transactionReceipt.transactionIndex,
                    from = transactionReceipt.from,
                    to = transactionReceipt.to,
                    cumulativeGasUsed = transactionReceipt.cumulativeGasUsed,
                    gasUsed = transactionReceipt.gasUsed,
                    contractAddress = transactionReceipt.contractAddress,
                    status = transactionReceipt.status == "1",
                    root = transactionReceipt.root,
                )
            )
        }
    }

    private fun <V, T : Response<V>, R> T.ifSuccess(mapper: T.() -> R): Result<R> {
        return ifSuccessResult {
            Result.success(mapper())
        }
    }

    private fun <V, T : Response<V>, R> T.ifSuccessResult(mapper: T.() -> Result<R>): Result<R> {
        if (hasError()) {
            return Result.failure(Exception("${error.code}: ${error.message}"))
        }
        return mapper()
    }

    private fun Response<String>.parseValues(outParams: List<TypeReference<Type<*>>>): List<Any> {
        if (outParams.isEmpty()) {
            return emptyList()
        }
        return FunctionReturnDecoder.decode(result, outParams).map { it.value }
    }
}
