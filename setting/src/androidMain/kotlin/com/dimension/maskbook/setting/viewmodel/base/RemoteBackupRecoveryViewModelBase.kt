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
package com.dimension.maskbook.setting.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.defaultCountDownTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

abstract class RemoteBackupRecoveryViewModelBase : ViewModel() {

    private val _value = MutableStateFlow("")
    val value = _value.asStateIn(viewModelScope)

    abstract val valueValid: StateFlow<Boolean>

    private val _code = MutableStateFlow("")
    val code = _code.asStateIn(viewModelScope)

    protected val _codeValid = MutableStateFlow(true)
    val codeValid = _codeValid.asStateIn(viewModelScope)

    private val _countdown = MutableStateFlow(0)
    val countdown = _countdown.asStateIn(viewModelScope)

    private val _canSend = MutableStateFlow(true)
    val canSend = _canSend.asStateIn(viewModelScope)

    protected val _loading = MutableStateFlow(false)
    val loading = _loading.asStateIn(viewModelScope)

    fun setValue(value: String) {
        _value.value = value
        _codeValid.value = true
    }

    fun setCode(value: String) {
        _code.value = value
    }

    fun startCountDown() = viewModelScope.launch {
        _canSend.value = false

        _countdown.value = defaultCountDownTime
        while (_countdown.value > 0) {
            delay(1.seconds)
            _countdown.value -= 1
        }

        _canSend.value = true
    }

    suspend fun sendCodeNow(code: String): Result<Unit> {
        return try {
            _loading.value = true

            startCountDown()
            sendCodeInternal(code)

            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        } finally {
            _loading.value = false
        }
    }

    abstract suspend fun sendCodeInternal(value: String)
}
