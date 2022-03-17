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
package com.dimension.maskbook.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BackupCloudViewModel(
    private val settingsRepository: ISettingsRepository,
) : ViewModel() {
    val meta = flow {
        emit(settingsRepository.provideBackupMeta())
    }.asStateIn(viewModelScope, null)
    private val _backupPassword = MutableStateFlow("")
    val backupPassword = _backupPassword.asStateIn(viewModelScope, "")
    val backupPasswordValid = combine(
        settingsRepository.backupPassword,
        _backupPassword
    ) { actual, input -> actual == input }
    fun setBackupPassword(value: String) {
        _backupPassword.value = value
    }
    private val _paymentPassword = MutableStateFlow("")
    val paymentPassword = _paymentPassword.asStateIn(viewModelScope, "")
    val paymentPasswordValid = combine(
        settingsRepository.paymentPassword,
        _paymentPassword
    ) { actual, input -> actual == input }
    fun setPaymentPassword(value: String) {
        _paymentPassword.value = value
    }
    private val _withLocalWallet = MutableStateFlow(false)
    val withLocalWallet = _withLocalWallet.asStateIn(viewModelScope, false)
    fun setWithLocalWallet(value: Boolean) {
        _withLocalWallet.value = value
    }
}
