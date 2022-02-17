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
import com.dimension.maskbook.setting.defaultRegionCode
import com.dimension.maskbook.setting.repository.BackupRepository
import com.dimension.maskbook.setting.repository.ISettingsRepository
import com.dimension.maskbook.setting.viewmodel.base.RemoteBackupRecoveryViewModelBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class EmailSetupViewModel(
    private val settingsRepository: ISettingsRepository,
    private val backupRepository: BackupRepository,
) : RemoteBackupRecoveryViewModelBase() {

    override val valueValid: StateFlow<Boolean>
        get() = value.map { email ->
            Validator.isEmail(email)
        }.asStateIn(viewModelScope, true)

    suspend fun verifyCodeNow(code: String, value: String): Result<Unit> {
        return try {
            _loading.value = true

            backupRepository.validateEmailCode(email = value, code = code)
            settingsRepository.saveEmailForCurrentPersona(value)

            Result.success(Unit)
        } catch (e: Throwable) {
            _codeValid.value = false
            Result.failure(e)
        } finally {
            _loading.value = false
        }
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendEmailCode(value)
    }
}

class PhoneSetupViewModel(
    private val settingsRepository: ISettingsRepository,
    private val backupRepository: BackupRepository,
) : RemoteBackupRecoveryViewModelBase() {

    private val _regionCode = MutableStateFlow(defaultRegionCode)
    val regionCode = _regionCode.asStateIn(viewModelScope)

    override val valueValid: StateFlow<Boolean>
        get() = combine(_regionCode, value) { regionCode, phone ->
            Validator.isPhone(regionCode + phone)
        }.asStateIn(viewModelScope, true)

    fun setRegionCode(value: String) {
        _regionCode.value = value
    }

    suspend fun verifyCodeNow(code: String, phone: String): Result<Unit> {
        return try {
            _loading.value = true

            backupRepository.validatePhoneCode(phone = phone, code = code)
            settingsRepository.savePhoneForCurrentPersona(phone)

            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        } finally {
            _loading.value = false
        }
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendPhoneCode(value)
    }
}
