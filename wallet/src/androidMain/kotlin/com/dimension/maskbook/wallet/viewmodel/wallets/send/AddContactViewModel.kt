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
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.wallet.usecase.Result
import com.dimension.maskbook.wallet.usecase.address.AddContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AddContactViewModel(
    private val addContactUseCase: AddContactUseCase,
) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateIn(viewModelScope, "")
    fun setName(value: String) {
        _name.value = value
    }

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateIn(viewModelScope)

    fun confirm(name: String, address: String, onResult: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            addContactUseCase(name = name, address = address).collect {
                when (it) {
                    is Result.Failed -> {
                        onResult.invoke(false)
                        _loadingState.value = false
                    }
                    is Result.Loading -> _loadingState.value = true
                    is Result.Success -> {
                        onResult.invoke(true)
                        _loadingState.value = false
                    }
                }
            }
        }
    }
}
