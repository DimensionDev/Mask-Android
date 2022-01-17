package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.utils.BiometricAuthenticator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class BackUpPasswordViewModel(
    settingsRepository: ISettingsRepository,
    biometricAuthenticator: BiometricAuthenticator
): BiometricViewModel(biometricAuthenticator, settingsRepository) {
    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")
    fun setPassword(value: String) {
        _password.value = value
    }

    val passwordValid by lazy {
        combine(settingsRepository.backupPassword, _password) { current, input ->
            current == input
        }
    }
}