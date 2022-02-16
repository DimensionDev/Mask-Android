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
import com.dimension.maskbook.setting.services.model.DownloadResponse
import com.dimension.maskbook.setting.viewmodel.base.RemoteBackupRecoveryViewModelBase
import kotlinx.coroutines.flow.MutableStateFlow

class EmailBackupViewModel(
    private val backupRepository: BackupRepository,
) : RemoteBackupRecoveryViewModelBase() {

    suspend fun verifyCodeNow(code: String, email: String, skipValidate: Boolean = false): Result<DownloadResponse> {
        return try {
            _loading.value = true

            if (!skipValidate) {
                backupRepository.validateEmailCode(email = email, code = code)
            }
            val target = backupRepository.getBackupInformationByEmail(email = email, code = code)

            Result.success(target)
        } catch (e: Throwable) {
            Result.failure(e)
        } finally {
            _loading.value = false
        }
    }

    override fun validate(value: String): Boolean {
        return Validator.isEmail(value)
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

    fun setRegionCode(value: String) {
        _regionCode.value = value
    }

    suspend fun verifyCodeNow(code: String, phone: String, skipValidate: Boolean = false): Result<DownloadResponse> {
        return try {
            _loading.value = true

            if (!skipValidate) {
                backupRepository.validatePhoneCode(phone = phone, code = code)
            }
            val target = backupRepository.getBackupInformationByPhone(phone = phone, code = code)

            Result.success(target)
        } catch (e: Throwable) {
            Result.failure(e)
        } finally {
            _loading.value = false
        }
    }

    override fun validate(value: String): Boolean {
        return Validator.isPhone(_regionCode.value + value)
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendPhoneCode(value)
    }
}
