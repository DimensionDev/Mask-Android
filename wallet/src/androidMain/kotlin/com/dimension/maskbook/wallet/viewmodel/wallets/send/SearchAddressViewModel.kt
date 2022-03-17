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
import com.dimension.maskbook.common.ext.Validator
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.usecase.Result
import com.dimension.maskbook.wallet.usecase.address.AddRecentAddressUseCase
import com.dimension.maskbook.wallet.usecase.address.GetContactsUseCase
import com.dimension.maskbook.wallet.usecase.address.GetEnsAddressUseCase
import com.dimension.maskbook.wallet.usecase.address.GetRecentAddressUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

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

@KoinViewModel
class SearchAddressViewModel(
    getRecentAddressUseCase: GetRecentAddressUseCase,
    getContactsUseCase: GetContactsUseCase,
    getEnsAddressUseCase: GetEnsAddressUseCase,
    private val addRecentAddressUseCase: AddRecentAddressUseCase,
) : ViewModel() {

    private val _input = MutableStateFlow("")
    val input = _input.asStateIn(viewModelScope, "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val recent = _input.flatMapLatest {
        getRecentAddressUseCase(filter = it)
            .map { result ->
                when (result) {
                    is Result.Success -> result.value
                    else -> emptyList()
                }
            }
    }.asStateIn(viewModelScope, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val contacts = _input.flatMapLatest {
        getContactsUseCase(filter = it)
            .map { result ->
                when (result) {
                    is Result.Success -> result.value
                    else -> emptyList()
                }
            }
    }.asStateIn(viewModelScope, emptyList())

    private val _selectEnsData = MutableStateFlow<EnsData.Success?>(null)
    val selectEnsData = _selectEnsData.asStateIn(viewModelScope, null)

    @OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
    val ensData = input.debounce(400)
        .filter { _selectEnsData.value == null && input.value.isNotEmpty() }
        .flatMapLatest { name ->
            getEnsAddressUseCase(chainType = ChainType.eth, ensName = name)
                .map { result ->
                    when (result) {
                        is Result.Failed -> EnsData.Failure(result.cause)
                        is Result.Loading -> EnsData.Loading
                        is Result.Success -> EnsData.Success(
                            name = name,
                            address = result.value
                        )
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
            addRecentAddressUseCase(
                address = address,
                name = name
            ).collect()
        }
    }
}
