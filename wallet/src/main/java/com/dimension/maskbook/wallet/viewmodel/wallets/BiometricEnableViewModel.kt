package com.dimension.maskbook.wallet.viewmodel.wallets

import android.content.Context
import androidx.lifecycle.ViewModel
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.utils.BiometricAuthenticator

class BiometricEnableViewModel(
    private val biometricAuthenticator: BiometricAuthenticator,
    private val repository: ISettingsRepository,
) : ViewModel() {
    fun enable(
        context: Context,
        title: Int,
        subTitle: Int = -1,
        negativeButton: Int,
        onEnable: () -> Unit = {},
    ) {
        biometricAuthenticator.biometricAuthenticate(
            context = context,
            onSuccess = {
                onEnable.invoke()
                repository.setBiometricEnabled(true)
            },
            title = title,
            subTitle = subTitle,
            negativeButtonText = negativeButton,
        )
    }

    fun isSupported(context: Context) = biometricAuthenticator.canAuthenticate(context = context)
}