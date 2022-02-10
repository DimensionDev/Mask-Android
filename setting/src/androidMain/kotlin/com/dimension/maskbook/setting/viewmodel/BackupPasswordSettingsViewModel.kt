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
import com.dimension.maskbook.common.ext.Validator
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class BackupPasswordSettingsViewModel(
    private val repository: ISettingsRepository,
) : ViewModel() {

    val currentPassword by lazy {
        repository.backupPassword.asStateIn(viewModelScope, "")
    }

    val isNext by lazy {
        combine(
            repository.backupPassword,
            _isNext
        ) { currentPassword, isNext ->
            currentPassword.isEmpty() || isNext
        }
    }

    private val _isNext = MutableStateFlow(false)

    fun goToNext() {
        _isNext.value = true
    }

    val confirmPassword by lazy {
        combine(
            repository.backupPassword,
            _password,
        ) { currentPassword, password ->
            currentPassword == password
        }
    }

    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")

    fun setPassword(value: String) {
        _password.value = value
    }

    private val _newPassword = MutableStateFlow("")
    val newPassword = _newPassword.asStateIn(viewModelScope, "")

    fun setNewPassword(value: String) {
        _newPassword.value = value
    }

    private val _newPasswordConfirm = MutableStateFlow("")
    val newPasswordConfirm = _newPasswordConfirm.asStateIn(viewModelScope, "")

    fun setNewPasswordConfirm(value: String) {
        _newPasswordConfirm.value = value
    }

    val confirmNewPassword by lazy {
        combine(
            newPassword,
            newPasswordConfirm
        ) { newPassword, newPasswordConfirm ->
            newPassword.isNotEmpty() &&
                newPassword == newPasswordConfirm &&
                Validator.isValidPasswordFormat(newPassword)
        }
    }

    fun confirm() {
        repository.setBackupPassword(newPassword.value)
    }
}
