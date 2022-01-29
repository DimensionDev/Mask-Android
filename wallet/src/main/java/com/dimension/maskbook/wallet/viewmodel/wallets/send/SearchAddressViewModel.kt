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
package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.Validator
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.IWalletContactRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

sealed class EnsData {
    object Loading : EnsData()
    data class Success(
        val name: String,
        val address: String,
    ) : EnsData()
    data class Failure(
        val exception: Exception
    ) : EnsData()
}

class SearchAddressViewModel(
    private val walletRepository: IWalletRepository,
    private val sendHistoryRepository: ISendHistoryRepository,
    private val walletContactRepository: IWalletContactRepository,
) : ViewModel() {

    val contacts by lazy {
        combine(walletContactRepository.contacts, input) { contacts, input ->
            if (input.isEmpty()) {
                contacts
            } else {
                contacts.filter {
                    it.name?.contains(input, ignoreCase = true) == true ||
                        it.address.contains(input, ignoreCase = true)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    val recent by lazy {
        combine(sendHistoryRepository.recent, input) { recent, input ->
            if (input.isEmpty()) {
                recent
            } else {
                recent.filter {
                    it.name?.contains(input, ignoreCase = true) == true ||
                        it.address.contains(input, ignoreCase = true)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private val _input = MutableStateFlow("")
    val input = _input.asStateIn(viewModelScope, "")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val ensData = _input.debounce(400).flatMapLatest { name ->
        flow {
            if (!Validator.isEnsName(name)) {
                emit(null)
                return@flow
            }

            emit(EnsData.Loading)

            val address = try {
                walletRepository.getEnsAddress(ChainType.eth, name)
            } catch (e: Exception) {
                emit(EnsData.Failure(e))
                return@flow
            }

            emit(
                EnsData.Success(
                    name = name,
                    address = address,
                )
            )
        }
    }.asStateIn(viewModelScope, null)

    private val _selectEnsData = MutableStateFlow<EnsData.Success?>(null)
    val selectEnsData = _selectEnsData.asStateIn(viewModelScope, null)

    fun onSelectEns(ensData: EnsData.Success) {
        _input.value = "${ensData.name}(" +
            ensData.address.substring(0, 7) +
            "..." +
            ensData.address.substring(ensData.address.length - 4) +
            ")"
        _selectEnsData.value = ensData
    }

    val canConfirm = combine(_selectEnsData, input) { ens, input ->
        ens != null || Validator.isWalletAddress(input)
    }.asStateIn(viewModelScope, false)

    fun onInputChanged(value: String) {
        _input.value = value
        _selectEnsData.value = null
    }

    fun addSendHistory(address: String, name: String) {
        sendHistoryRepository.addOrUpdate(address, name)
    }
}
