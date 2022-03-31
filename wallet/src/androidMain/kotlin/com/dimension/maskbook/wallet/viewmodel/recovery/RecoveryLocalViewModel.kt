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
import com.dimension.maskbook.setting.export.model.BackupMeta
import com.dimension.maskbook.setting.export.model.BackupMetaFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecoveryLocalViewModel(
    private val backupServices: BackupServices,
    private val uri: String,
    private val account: String,
    private val contentResolver: ContentResolver,
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
    private var file: BackupMetaFile? = null

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
        try {
            val data = contentResolver.openInputStream(Uri.parse(uri))?.use {
                backupServices.decryptBackup(_password.value, account, it.readBytes())
            } ?: run {
                _loadState.value = LoadState.Failed
                return@launch
            }
            file = data
            _meta.value = backupServices.provideBackupMeta(data)
            _loadState.value = LoadState.Success
        } catch (e: Throwable) {
            e.printStackTrace()
            _passwordError.value = true
            _loadState.value = LoadState.RequirePassword
        }
    }

    fun restore() = viewModelScope.launch {
        try {
            file?.let { backupServices.restoreBackup(it) }
        } catch (e: Throwable) {
        }
    }
}
