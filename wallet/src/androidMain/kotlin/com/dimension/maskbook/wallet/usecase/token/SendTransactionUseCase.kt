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
package com.dimension.maskbook.wallet.usecase.token

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface SendTransactionUseCase {
    operator fun invoke(
        amount: BigDecimal,
        address: String,
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double,
        data: String,
        chainType: ChainType? = null, // if null use current chain
    ): Flow<Result<String>>
}

class SendTransactionUseCaseImpl(
    val repository: IWalletRepository,
) : SendTransactionUseCase {
    private val result = MutableStateFlow<Result<String>>(Result.Loading())
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun invoke(
        amount: BigDecimal,
        address: String,
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double,
        data: String,
        chainType: ChainType?,
    ): Flow<Result<String>> {
        scope.launch {
            try {
                result.value = Result.Loading()
                repository.transactionWithCurrentWalletAndChainType(
                    amount = amount,
                    address = address,
                    chainType = chainType ?: repository.currentChain.first()?.chainType
                        ?: throw Error("Current chain is null"),
                    gasLimit = gasLimit,
                    maxFee = maxFee,
                    maxPriorityFee = maxPriorityFee,
                    data = data,
                    onDone = {
                        result.value = it?.let {
                            Result.Success(it)
                        } ?: Result.Failed(Error("Failed to send transaction"))
                    },
                    onError = { result.value = Result.Failed(it) }
                )
            } catch (e: Throwable) {
                result.value = Result.Failed(e)
            }
        }
        return result
    }
}
