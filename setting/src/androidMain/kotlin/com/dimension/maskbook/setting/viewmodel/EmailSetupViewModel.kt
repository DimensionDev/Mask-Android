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

import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.Validator
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.repository.BackupRepository
import com.dimension.maskbook.setting.repository.ISettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EmailSetupViewModel(
    private val settingsRepository: ISettingsRepository,
    private val backupRepository: BackupRepository,
    private val requestNavigate: (NavigateArgs) -> Unit,
) : RemoteBackupRecoveryViewModelBase(
    requestNavigate
) {
    override fun verifyCode(code: String, value: String, skipValidate: Boolean): Job = viewModelScope.launch {
        _loading.value = true
        try {
            backupRepository.validateEmailCode(email = value, code = code)
            settingsRepository.saveEmailForCurrentPersona(value)
            requestNavigate.invoke(NavigateArgs(value, NavigateTarget.Next))
        } catch (e: Throwable) {
            _codeValid.value = false
        }
        _loading.value = false
    }

    override suspend fun downloadBackupInternal(code: String, value: String): String {
        throw NotImplementedError()
    }

    override suspend fun verifyCodeInternal(value: String, code: String) {
        throw NotImplementedError()
    }

    override fun validate(value: String): Boolean {
        return Validator.isEmail(value)
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendEmailCode(value)
    }
}

class PhoneSetupViewModel(
    private val settingsRepository: ISettingsRepository,
    private val requestNavigate: (NavigateArgs) -> Unit,
    private val backupRepository: BackupRepository,
) : RemoteBackupRecoveryViewModelBase(
    requestNavigate
) {
    private val _regionCode = MutableStateFlow("+86")
    val regionCode = _regionCode.asStateIn(viewModelScope, "+86")
    fun setRegionCode(value: String) {
        _regionCode.value = value
    }

    override fun verifyCode(code: String, value: String, skipValidate: Boolean): Job = viewModelScope.launch {
        _loading.value = true
        try {
            backupRepository.validatePhoneCode(phone = value, code = code)
            settingsRepository.savePhoneForCurrentPersona(value)
            requestNavigate.invoke(NavigateArgs(value, NavigateTarget.Next))
        } catch (e: Throwable) {
            _codeValid.value = false
        }
        _loading.value = false
    }

    override suspend fun downloadBackupInternal(code: String, value: String): String {
        throw NotImplementedError()
    }

    override suspend fun verifyCodeInternal(value: String, code: String) {
        throw NotImplementedError()
    }

    override fun validate(value: String): Boolean {
        return Validator.isPhone(_regionCode.value + value)
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendPhoneCode(value)
    }
}
