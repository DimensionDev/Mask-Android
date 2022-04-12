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

import com.dimension.maskbook.common.ext.Validator
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.usecase.AddRecentAddressUseCase
import com.dimension.maskbook.wallet.usecase.GetContactsUseCase
import com.dimension.maskbook.wallet.usecase.GetEnsAddressUseCase
import com.dimension.maskbook.wallet.usecase.GetRecentAddressUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

sealed class EnsData {
    object Loading : EnsData()
    data class Success(
        val name: String,
        val address: String,
    ) : EnsData()

    data class Failure(
        val exception: Throwable
    ) : EnsData()
}

class SearchAddressViewModel(
    getRecentAddress: GetRecentAddressUseCase,
    getContacts: GetContactsUseCase,
    getEnsAddress: GetEnsAddressUseCase,
    private val addRecentAddress: AddRecentAddressUseCase,
) : ViewModel() {

    private val _input = MutableStateFlow("")
    val input = _input.asStateIn(viewModelScope, "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val recent = _input.flatMapLatest {
        getRecentAddress(filter = it)
    }.asStateIn(viewModelScope, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val contacts = _input.flatMapLatest {
        getContacts(filter = it)
    }.asStateIn(viewModelScope, emptyList())

    private val _selectEnsData = MutableStateFlow<EnsData.Success?>(null)
    val selectEnsData = _selectEnsData.asStateIn(viewModelScope, null)

    @OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
    val ensData = input.debounce(400)
        .filter { _selectEnsData.value == null && input.value.isNotEmpty() }
        .flatMapLatest { name ->
            flow {
                emit(EnsData.Loading)
                getEnsAddress(chainType = ChainType.eth, ensName = name)
                    .onSuccess {
                        emit(EnsData.Success(name = name, address = it))
                    }
                    .onFailure {
                        emit(EnsData.Failure(it))
                    }
            }
        }.asStateIn(viewModelScope, null)

    val canConfirm = combine(_selectEnsData, input) { ens, input ->
        ens != null || Validator.isWalletAddress(input)
    }.asStateIn(viewModelScope, false)

    fun onSelectEns(ensData: EnsData.Success) {
        _input.value = "${ensData.name}(" +
            ensData.address.substring(0, 7) +
            "..." +
            ensData.address.substring(ensData.address.length - 4) +
            ")"
        _selectEnsData.value = ensData
    }

    fun onInputChanged(value: String) {
        _input.value = value
        _selectEnsData.value = null
    }

    fun addSendHistory(address: String, name: String) {
        viewModelScope.launch {
            addRecentAddress(address = address, name = name)
        }
    }
}
