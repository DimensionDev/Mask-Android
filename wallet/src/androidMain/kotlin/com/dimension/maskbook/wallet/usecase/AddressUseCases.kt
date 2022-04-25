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
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.IWalletContactRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.map

class AddContactUseCase(
    private val repository: IWalletContactRepository
) {
    suspend operator fun invoke(address: String, name: String) =
        Result.of { repository.addOrUpdate(address = address, name = name) }
}

class AddRecentAddressUseCase(
    private val repository: ISendHistoryRepository
) {
    suspend operator fun invoke(address: String, name: String) =
        Result.of { repository.addOrUpdate(address = address, name = name) }
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
) {
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
    suspend operator fun invoke(chainType: ChainType, ensName: String) = Result.of {
        repository.getEnsAddress(
            chainType = chainType,
            name = ensName
        )
    }
}
