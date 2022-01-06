package com.dimension.maskbook.wallet.viewmodel.wallets

import android.content.Context
import androidx.lifecycle.ViewModel
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.utils.BiometricAuthenticator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class BiometricAuthenticateViewModel(
    private val biometricAuthenticator: BiometricAuthenticator,
    private val settingsRepository: ISettingsRepository
): ViewModel() {
    private var _enterPassword = MutableStateFlow(false)
    val biometricEnabled by lazy {
        combine(settingsRepository.biometricEnabled, _enterPassword) { enabled, enterPassword ->
            enabled && !enterPassword
        }
    }

    fun biometricAuthenticate(context: Context, onSuccess:() -> Unit) {
        biometricAuthenticator.biometricAuthenticate(
            context = context,
            negativeButtonText = "Enter Password",
            onResult = { if (it) onSuccess.invoke() },
            onCanceled = { _enterPassword.value = true }
        )
    }
}