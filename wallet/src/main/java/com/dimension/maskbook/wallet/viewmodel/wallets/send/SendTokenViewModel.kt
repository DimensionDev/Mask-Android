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
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull

class SendTokenViewModel(
    private val toAddress: String,
    private val sendHistoryRepository: ISendHistoryRepository,
    private val settingsRepository: ISettingsRepository,
) : ViewModel() {
    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")
    fun setPassword(value: String) {
        _password.value = value
    }

    val canConfirm by lazy {
        combine(settingsRepository.paymentPassword, _password) { current, input ->
            current == input
        }
    }

    private val _amount = MutableStateFlow("0")
    val amount = _amount.asStateIn(viewModelScope, "0")
    fun setAmount(value: String) {
        _amount.value = value
    }

    val addressData by lazy {
        sendHistoryRepository.getByAddress(toAddress)
            .asStateIn(viewModelScope, null)
            .mapNotNull { it }
    }
}
