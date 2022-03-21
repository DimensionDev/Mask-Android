package com.dimension.maskbook.wallet.usecase

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
    ) = runCatching {
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
