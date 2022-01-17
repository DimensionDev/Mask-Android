package com.dimension.maskbook.wallet.viewmodel.wallets

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.utils.BiometricAuthenticator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

open class BiometricViewModel(
    private val biometricAuthenticator: BiometricAuthenticator,
    protected val settingsRepository: ISettingsRepository
) : ViewModel() {
    private var _enterPassword = MutableStateFlow(false)
    val biometricEnabled by lazy {
        combine(settingsRepository.biometricEnabled, _enterPassword) { enabled, enterPassword ->
            enabled && !enterPassword
        }
    }

    fun authenticate(
        context: Context,
        title: String = "Unlock with biometrics",
        subtitle: String = "",
        onSuccess: (password: String) -> Unit
    ) {
        biometricAuthenticator.biometricAuthenticate(
            context = context,
            negativeButtonText = "Enter Password",
            onSuccess = {
                viewModelScope.launch {
                    onSuccess.invoke(getPassword())
                }
            },
            onFailed = {
              _enterPassword.value = true
            },
            onCanceled = { _enterPassword.value = true },
            title = title,
            subtitle = subtitle
        )
    }

    protected suspend fun getPassword() = settingsRepository.paymentPassword.first()
}