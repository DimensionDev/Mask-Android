package com.dimension.maskbook.wallet.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.BackupRepository
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BackupMergeConfirmViewModel(
    private val backupRepository: BackupRepository,
    private val settingsRepository: ISettingsRepository,
    private val onDone: () -> Unit,
): ViewModel() {
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

    fun confirm(type: String, downloadUrl: String) = viewModelScope.launch {
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