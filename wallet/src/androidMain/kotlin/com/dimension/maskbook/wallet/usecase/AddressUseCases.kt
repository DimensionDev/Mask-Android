package com.dimension.maskbook.wallet.usecase

import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.IWalletContactRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.SearchAddressData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AddContactUseCase(
    private val repository: IWalletContactRepository
) {
    suspend operator fun invoke(address: String, name: String) =
        runCatching { repository.addOrUpdate(address = address, name = name) }
}

class AddRecentAddressUseCase(
    private val repository: ISendHistoryRepository
) {
    suspend operator fun invoke(address: String, name: String) =
        runCatching { repository.addOrUpdate(address = address, name = name) }
}

class GetAddressUseCase(
    private val repository: ISendHistoryRepository
) {
    operator fun invoke(address: String, addIfNotExists: Boolean = false) = if (addIfNotExists) {
        repository.getOrCreateByAddress(address)
    } else {
        repository.getByAddress(address)
    }
}

class GetContactsUseCase(
    private val repository: IWalletContactRepository
) {

    operator fun invoke(filter: String = "") = repository.contacts.map { data ->
        if (filter.isEmpty()) {
            data
        } else {
            data.filter {
                it.name?.contains(filter, ignoreCase = true) == true ||
                    it.address.contains(filter, ignoreCase = true)
            }
        }
    }
}

class GetRecentAddressUseCase(
    private val repository: ISendHistoryRepository
)  {
    operator fun invoke(filter: String = "") = repository.recent.map { data ->
        if (filter.isEmpty()) {
            data
        } else {
            data.filter {
                it.name?.contains(filter, ignoreCase = true) == true ||
                    it.address.contains(filter, ignoreCase = true)
            }
        }
    }
}

class GetEnsAddressUseCase(
    private val repository: IWalletRepository
) {
    suspend operator fun invoke(chainType: ChainType, ensName: String) = runCatching {
        repository.getEnsAddress(
            chainType = chainType,
            name = ensName
        )
    }
}