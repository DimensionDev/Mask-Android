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

import com.dimension.maskbook.common.ext.Validator
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.repository.BackupRepository
import com.dimension.maskbook.setting.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class BackupMergeConfirmViewModel(
    private val backupRepository: BackupRepository,
    private val settingsRepository: ISettingsRepository,
    private val onDone: () -> Unit,
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateIn(viewModelScope, false)
    private val _backupPassword = MutableStateFlow("")
    val backupPassword = _backupPassword.asStateIn(viewModelScope, "")
    private val _passwordValid = MutableStateFlow(true)
    val passwordValid = _passwordValid.asStateIn(viewModelScope, true)
    private var file: File? = null

    fun setBackupPassword(value: String) {
        _backupPassword.value = value
        _passwordValid.value = Validator.isValidPasswordFormat(value)
    }

    fun confirm(downloadUrl: String, account: String) = viewModelScope.launch {
        _loading.value = true
        try {
            val content = file ?: run {
                val result = backupRepository.downloadFile(downloadUrl)
                file = result
                result
            }
            val backup = backupRepository.decryptBackup(backupPassword.value, account, content.readBytes())
            settingsRepository.restoreBackup(backup)
            onDone.invoke()
        } catch (e: Throwable) {
            _passwordValid.value = false
        }
        _loading.value = false
    }
}
