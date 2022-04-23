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
package com.dimension.maskbook.persona.viewmodel.recovery

import android.content.ContentResolver
import android.net.Uri
import com.dimension.maskbook.common.ext.Validator
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.export.BackupServices
import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.setting.export.model.BackupMeta
import com.dimension.maskbook.setting.export.model.BackupMetaFile
import com.dimension.maskbook.setting.export.model.BackupWrongPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class RecoveryLocalViewModel(
    private val backupServices: BackupServices,
    private val uri: String,
    private val account: String,
    private val contentResolver: ContentResolver,
    private val settingServices: SettingServices,
) : ViewModel() {
    enum class LoadState {
        Loading,
        Failed,
        RequirePassword,
        PasswordSuccess,
        Success,
    }

    private val _meta = MutableStateFlow<BackupMeta?>(null)
    val meta = _meta.asStateIn(viewModelScope, null)
    private val _loadState = MutableStateFlow(LoadState.Loading)
    val loadState = _loadState.asStateIn(viewModelScope, LoadState.Loading)
    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")
    private val _passwordError = MutableStateFlow(false)
    val passwordError = _passwordError.asStateIn(viewModelScope, false)
    private val _file = MutableStateFlow<BackupMetaFile?>(null)
    val file = _file.asStateIn(viewModelScope, null)
    val passwordValid = password.map { Validator.isValidBackupPasswordFormat(it) }
    val paymentPassword by lazy {
        settingServices.paymentPassword
    }

    init {
        loading()
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    private fun loading() = viewModelScope.launch {
        _loadState.value = LoadState.RequirePassword
    }

    fun confirmPassword() = viewModelScope.launch {
        _loadState.value = LoadState.Loading
        _passwordError.value = false
        try {
            val data = contentResolver.openInputStream(Uri.parse(uri))?.use {
                backupServices.decryptBackup(_password.value, account, it.readBytes())
            } ?: run {
                _loadState.value = LoadState.Failed
                return@launch
            }
            _file.value = data
            _meta.value = backupServices.provideBackupMeta(data)
            _loadState.value = LoadState.PasswordSuccess
        } catch (e: Throwable) {
            e.printStackTrace()
            if (e is BackupWrongPasswordException) {
                _passwordError.value = true
                _loadState.value = LoadState.RequirePassword
            } else {
                _loadState.value = LoadState.Failed
            }
        }
    }

    fun restore() = viewModelScope.launch {
        try {
            _loadState.value = LoadState.Loading
            _file.value?.let { backupServices.restoreBackup(it) }
            _loadState.value = LoadState.Success
        } catch (e: Throwable) {
            _loadState.value = LoadState.Failed
            e.printStackTrace()
        }
    }
}
