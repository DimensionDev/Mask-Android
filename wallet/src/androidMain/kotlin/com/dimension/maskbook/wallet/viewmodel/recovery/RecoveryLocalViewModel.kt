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
package com.dimension.maskbook.wallet.viewmodel.recovery

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.export.BackupServices
import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.setting.export.model.BackupMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class RecoveryLocalViewModel(
    private val backupServices: BackupServices,
    private val uri: String,
    private val account: String?,
    private val contentResolver: ContentResolver,
    private val settingServices: SettingServices,
) : ViewModel() {
    enum class LoadState {
        Loading,
        Failed,
        RequirePassword,
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
    private var json = ""

    init {
        loading()
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    private fun loading() = viewModelScope.launch {
        if (account != null) {
            _loadState.value = LoadState.RequirePassword
        } else {
            try {
                contentResolver.openInputStream(Uri.parse(uri))?.use {
                    json = it.bufferedReader().use { it.readText() }
                    _meta.value = backupServices.provideBackupMetaFromJson(json)
                    if (settingServices.backupPassword.firstOrNull().isNullOrEmpty()) {
                        _loadState.value = LoadState.Success
                    } else {
                        _loadState.value = LoadState.RequirePassword
                    }
                } ?: run {
                    _loadState.value = LoadState.Failed
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                _loadState.value = LoadState.Failed
            }
        }
    }

    fun confirmPassword() = viewModelScope.launch {
        if (account != null) {
            try {
                json = contentResolver.openInputStream(Uri.parse(uri))?.use {
                    backupServices.decryptBackup(_password.value, account, it.readBytes())
                } ?: run {
                    _loadState.value = LoadState.Failed
                    return@launch
                }
                _meta.value = backupServices.provideBackupMetaFromJson(json)
            } catch (e: Throwable) {
                e.printStackTrace()
                _passwordError.value = true
                _loadState.value = LoadState.RequirePassword
            }
        } else {
            if (settingServices.backupPassword.firstOrNull() == password.value) {
                _loadState.value = LoadState.Success
            } else {
                _passwordError.value = true
            }
        }
    }

    fun restore() = viewModelScope.launch {
        if (json.isNotEmpty()) {
            try {
                backupServices.restoreBackupFromJson(json)
            } catch (e: Throwable) {
            }
        }
    }
}
