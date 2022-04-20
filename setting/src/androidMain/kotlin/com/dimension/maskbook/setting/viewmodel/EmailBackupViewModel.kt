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
import com.dimension.maskbook.setting.export.model.BackupFileMeta
import com.dimension.maskbook.setting.repository.BackupRepository
import com.dimension.maskbook.setting.viewmodel.base.RemoteBackupRecoveryViewModelBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class EmailBackupViewModel(
    private val backupRepository: BackupRepository,
) : RemoteBackupRecoveryViewModelBase() {

    override val valueValid: StateFlow<Boolean>
        get() = value.map { email ->
            Validator.isEmail(email)
        }.asStateIn(viewModelScope, true)

    suspend fun verifyCodeNow(code: String, email: String, skipValidate: Boolean = false): Result<BackupFileMeta> {
        return try {
            _loading.value = true

            if (!skipValidate) {
                backupRepository.validateEmailCode(email = email, code = code)
            }
            val target = backupRepository.downloadBackupWithEmail(email = email, code = code)

            Result.success(target)
        } catch (e: Throwable) {
            // code is correct but no backup data found
            if (e is HttpException && e.code() == 404) {
                Result.success(BackupFileMeta("", null, null, null))
            } else {
                Result.failure(e)
            }
        } finally {
            _loading.value = false
        }
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendEmailCode(value)
    }
}

class PhoneBackupViewModel(
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

    suspend fun verifyCodeNow(code: String, phone: String, skipValidate: Boolean = false): Result<BackupFileMeta> {
        return try {
            _loading.value = true

            if (!skipValidate) {
                backupRepository.validatePhoneCode(phone = phone, code = code)
            }
            val target = backupRepository.downloadBackupWithPhone(phone = phone, code = code)

            Result.success(target)
        } catch (e: Throwable) {
            // code is correct but no backup data found
            if (e is HttpException && e.code() == 404) {
                Result.success(BackupFileMeta("", null, null, null))
            } else {
                Result.failure(e)
            }
        } finally {
            _loading.value = false
        }
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendPhoneCode(value)
    }
}
