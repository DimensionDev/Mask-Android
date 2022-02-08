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
package com.dimension.maskbook.wallet.viewmodel.wallets.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class WalletDeleteViewModel(
    private val id: String,
    private val settingsRepository: SettingServices,
    private val walletRepository: IWalletRepository,
) : ViewModel() {
    val wallet by lazy {
        walletRepository.wallets.map {
            it.firstOrNull { it.id == id }
        }.asStateIn(viewModelScope, null).mapNotNull { it }
    }
    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")
    fun setPassword(value: String) {
        _password.value = value
    }
    val canConfirm by lazy {
        combine(_password, settingsRepository.paymentPassword) { input, actual ->
            input == actual
        }
    }

    fun confirm() {
        walletRepository.deleteWallet(id)
    }
}
