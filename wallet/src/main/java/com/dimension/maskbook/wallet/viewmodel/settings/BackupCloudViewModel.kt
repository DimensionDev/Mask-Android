package com.dimension.maskbook.wallet.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class BackupCloudViewModel(
    private val settingsRepository: ISettingsRepository,
) : ViewModel() {
    val meta = flow {
        emit(settingsRepository.provideBackupMeta())
    }.asStateIn(viewModelScope, null)
    private val _backupPassword = MutableStateFlow("")
    val backupPassword = _backupPassword.asStateIn(viewModelScope, "")
    val backupPasswordValid = combine(
        settingsRepository.backupPassword,
        _backupPassword
    ) { actual, input -> actual == input }
    fun setBackupPassword(value: String) {
        _backupPassword.value = value
    }
    private val _paymentPassword = MutableStateFlow("")
    val paymentPassword = _paymentPassword.asStateIn(viewModelScope, "")
    val paymentPasswordValid = combine(
        settingsRepository.paymentPassword,
        _paymentPassword
    ) { actual, input -> actual == input }
    fun setPaymentPassword(value: String) {
        _paymentPassword.value = value
    }
    private val _withLocalWallet = MutableStateFlow(false)
    val withLocalWallet = _withLocalWallet.asStateIn(viewModelScope, false)
    fun setWithLocalWallet(value: Boolean) {
        _withLocalWallet.value = value
    }
}

