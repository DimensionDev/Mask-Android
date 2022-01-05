package com.dimension.maskbook.wallet.viewmodel.wallets

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.utils.BiometricAuthenticator

class BiometricEnableViewModel(
    private val biometricAuthenticator: BiometricAuthenticator,
    private val repository: ISettingsRepository,
) : ViewModel() {
    fun enable(
        context: Context,
        title: String = "",
        subTitle: String = "",
        negativeButton: String = "",
        onEnable: () -> Unit = {},
    ) {
        biometricAuthenticator.biometricAuthenticate(
            context = context,
            onResult = {
                if (it) {
                    onEnable.invoke()
                    repository.setBiometricEnabled(true)
                }
            },
            title = title,
            subtitle = subTitle,
            negativeButtonText = negativeButton,
        )
    }

    fun isSupported(context: Context) = biometricAuthenticator.canAuthenticate(context = context)
}