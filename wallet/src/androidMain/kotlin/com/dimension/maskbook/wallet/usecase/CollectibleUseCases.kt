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

import com.dimension.maskbook.common.ext.of
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GetWalletCollectibleCollectionsUseCase(
    val repository: ICollectibleRepository,
    val walletRepository: IWalletRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() = walletRepository.currentWallet.filterNotNull().flatMapLatest {
        repository.getCollectibleCollectionsByWallet(it)
    }
}

class GetWalletCollectibleUseCase(
    val repository: ICollectibleRepository,
) {
    operator fun invoke(id: String) = repository.getCollectibleById(id)
}

class SendWalletCollectibleUseCase(
    private val repository: IWalletRepository,
) {
    suspend operator fun invoke(
        address: String,
        collectible: WalletCollectibleData,
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double
    ) = Result.of {
        suspendCoroutine<String> { continuation ->
            repository.sendCollectibleWithCurrentWallet(
                address = address,
                collectible = collectible,
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
