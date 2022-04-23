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

import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.onFinished
import com.dimension.maskbook.wallet.usecase.AddContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class AddContactViewModel(
    private val addContact: AddContactUseCase,
) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateIn(viewModelScope, "")
    fun setName(value: String) {
        _name.value = value
    }

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateIn(viewModelScope)

    fun confirm(name: String, address: String, onResult: (success: Boolean) -> Unit) {
        _loadingState.value = true
        viewModelScope.launch {
            addContact(name = name, address = address).onSuccess {
                onResult.invoke(true)
            }.onFailure {
                onResult.invoke(false)
            }.onFinished {
                _loadingState.value = false
            }
        }
    }
}
