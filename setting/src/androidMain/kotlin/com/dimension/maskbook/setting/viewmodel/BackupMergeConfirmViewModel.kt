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
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.repository.BackupRepository
import com.dimension.maskbook.setting.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class BackupMergeConfirmViewModel(
    private val backupRepository: BackupRepository,
    private val settingsRepository: ISettingsRepository,
    @InjectedParam private val onDone: () -> Unit,
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateIn(viewModelScope, false)
    private val _backupPassword = MutableStateFlow("")
    val backupPassword = _backupPassword.asStateIn(viewModelScope, "")
    val passwordValid = combine(_backupPassword, settingsRepository.backupPassword) { input, actual ->
        input == actual
    }

    fun setBackupPassword(value: String) {
        _backupPassword.value = value
    }

    fun confirm(downloadUrl: String) = viewModelScope.launch {
        _loading.value = true
        try {
            // TODO: decrypt .bin backup
            val content = backupRepository.downloadFile(downloadUrl).readText()
            settingsRepository.restoreBackupFromJson(content)
            onDone.invoke()
        } catch (e: Throwable) {
        }
        _loading.value = false
    }
}
