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
package com.dimension.maskbook.wallet.usecase

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SendTokenUseCase(
    private val repository: IWalletRepository,
) {
    suspend operator fun invoke(
        amount: BigDecimal,
        address: String,
        tokenData: TokenData,
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double
    ) = runCatching {
        suspendCoroutine<String> { continuation ->
            repository.sendTokenWithCurrentWallet(
                amount = amount,
                address = address,
                tokenData = tokenData,
                gasLimit = gasLimit,
                maxFee = maxFee,
                maxPriorityFee = maxPriorityFee,
                onError = {
                    continuation.resumeWithException(it)
                },
                onDone = {
                    continuation.resume(it.orEmpty())
                }
            )
        }
    }
}

class SendTransactionUseCase(
    private val repository: IWalletRepository,
) {
    suspend operator fun invoke(
        amount: BigDecimal,
        address: String,
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double,
        data: String,
        chainType: ChainType?,
    ) = runCatching {
        val chain = chainType ?: repository.currentChain.first()?.chainType
        suspendCoroutine<String> { continuation ->
            repository.transactionWithCurrentWalletAndChainType(
                amount = amount,
                address = address,
                chainType = chain
                    ?: throw Error("Current chain is null"),
                gasLimit = gasLimit,
                maxFee = maxFee,
                maxPriorityFee = maxPriorityFee,
                data = data,
                onDone = {
                    it?.let {
                        continuation.resume(it)
                    } ?: continuation.resumeWithException(Error("Failed to send transaction"))
                },
                onError = { continuation.resumeWithException(it) }
            )
        }
    }
}

class GetWalletTokensUseCase(
    private val repository: IWalletRepository,
) {

    operator fun invoke(
        filterWithChainType: Boolean = true,
        chainType: ChainType? = null
    ) = combine(repository.currentWallet, repository.currentChain) { wallet, currentChain ->
        val filterChainType = if (filterWithChainType) chainType
            ?: currentChain?.chainType else null
        wallet?.tokens?.filter {
            if (filterChainType == null) true else it.tokenData.chainType == filterChainType
        } ?: emptyList()
    }.catch {
        emit(emptyList())
    }
}

class GetWalletTokenByAddressUseCase(
    private val repository: IWalletRepository,
) {
    operator fun invoke(tokenAddress: String) = repository.currentWallet.map {
        it?.tokens?.firstOrNull { token ->
            token.tokenAddress == tokenAddress
        }
    }
}

class GetWalletNativeTokenUseCase(
    private val repository: IWalletRepository
) {
    operator fun invoke(chainType: ChainType? = null) = flow {
        try {
            val currentChain = repository.currentChain.firstOrNull()
            val token = if (chainType == null || chainType == currentChain?.chainType) {
                currentChain?.nativeToken
            } else {
                repository.getChainData(chainType).firstOrNull()?.nativeToken
            }
            token?.let { nativeToken ->
                repository.currentWallet.firstOrNull()?.tokens?.firstOrNull {
                    it.tokenAddress == nativeToken.address
                } ?: WalletTokenData(
                    count = BigDecimal.ZERO,
                    tokenAddress = nativeToken.address,
                    tokenData = nativeToken
                )
            }.let {
                emit(it)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            emit(null)
        }
    }
}
