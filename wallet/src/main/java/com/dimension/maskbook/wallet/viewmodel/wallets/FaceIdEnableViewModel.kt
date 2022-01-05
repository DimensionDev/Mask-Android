package com.dimension.maskbook.wallet.viewmodel.wallets

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.dimension.maskbook.wallet.utils.BiometricAuthenticator

class FaceIdEnableViewModel(private val biometricAuthenticator: BiometricAuthenticator) : ViewModel() {
    fun enable(
        context: Context,
        title: String,
        subTitle: String,
        negativeButton: String,
        onEnable: () -> Unit,
    ) {
        biometricAuthenticator.biometricAuthenticate(
            context = context,
            onResult = { if (it) onEnable.invoke() },
            title = title,
            subtitle = subTitle,
            negativeButtonText = negativeButton,
            onCanceled = {
                Log.d("biometric", "onCanceled")
            }
        )
    }

    fun isSupported(context: Context) = biometricAuthenticator.canAuthenticate(context = context)
}