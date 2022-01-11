package com.dimension.maskbook.wallet.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

class BiometricAuthenticator {
    fun canAuthenticate(context: Context): Boolean {
        return BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun biometricAuthenticate(
        context: Context,
        title: String,
        subtitle: String = "",
        negativeButtonText: String = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_cancel),
        onSuccess: () -> Unit,
        onFailed: (errString:String) -> Unit =  {},
        onCanceled: () -> Unit = {}
    ) {
        context.findActivity()?.let {
            val biometricPrompt = BiometricPrompt(it, object :
                BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess.invoke()
                }

                override fun onAuthenticationFailed() {
                    onFailed.invoke("Can't recognize")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_CANCELED, BiometricPrompt.ERROR_USER_CANCELED, BiometricPrompt.ERROR_NEGATIVE_BUTTON -> onCanceled.invoke()
                        else -> onFailed.invoke(errString.toString())
                    }
                }
            })
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(BIOMETRIC_STRONG)
                .build()
            try {
                biometricPrompt.authenticate(promptInfo)
            } catch (e: Exception) {
                onFailed(e.toString())
            }
        } ?: onFailed.invoke("Can't find any Activity to support BiometricPrompt")
    }
}

private fun Context.findActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
