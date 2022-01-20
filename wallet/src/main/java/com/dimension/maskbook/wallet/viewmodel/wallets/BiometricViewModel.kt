package com.dimension.maskbook.wallet.viewmodel.wallets

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.R
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
        title: Int = R.string.scene_biometry_id_face_id,
        subTitle: Int = -1,
        onSuccess: (password: String) -> Unit
    ) {
        biometricAuthenticator.biometricAuthenticate(
            context = context,
            negativeButtonText = R.string.scene_create_wallet_enter_password,
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
            subTitle = subTitle
        )
    }

    protected suspend fun getPassword() = settingsRepository.paymentPassword.first()
}