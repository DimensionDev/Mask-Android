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

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.LocalBackupAccount
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.repository.BackupRepository
import com.dimension.maskbook.setting.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class BackupLocalViewModel(
    private val viewModelCoroutineContext: CoroutineContext,
    private val repository: ISettingsRepository,
    private val backupRepository: BackupRepository,
) : ViewModel() {

    enum class State {
        Normal,
        Loading,
        Failed,
        Success,
    }

    private val _state = MutableStateFlow(State.Normal)
    val state = _state.asStateIn(viewModelScope, State.Normal)
    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")
    fun setPassword(value: String) {
        _password.value = value
    }
    val backupPasswordValid = combine(
        repository.backupPassword,
        _password
    ) { actual, input -> actual == input }

    private val _withWallet = MutableStateFlow(false)
    val withWallet = _withWallet.asStateIn(viewModelScope, false)
    fun setWithWallet(value: Boolean) {
        _withWallet.value = value
    }
    private val _paymentPassword = MutableStateFlow("")
    val paymentPassword = _paymentPassword.asStateIn(viewModelScope, "")
    val paymentPasswordValid = combine(
        repository.paymentPassword,
        _paymentPassword
    ) { actual, input -> actual == input }
    fun setPaymentPassword(value: String) {
        _paymentPassword.value = value
    }

    fun save(it: Uri, withWallet: Boolean) = viewModelScope.launch(viewModelCoroutineContext) {
        _state.value = State.Loading
        try {
            val password = repository.backupPassword.firstOrNull() ?: run {
                _state.value = State.Failed
                return@launch
            }
            val json = repository.createBackup(noWallets = !withWallet)
            backupRepository.saveLocality(it, json, password = password, account = LocalBackupAccount)
            _state.value = State.Success
        } catch (e: Throwable) {
            e.printStackTrace()
            _state.value = State.Failed
        }
    }

    val meta = flow {
        emit(repository.generateBackupMeta())
    }.asStateIn(viewModelScope, null)
}
