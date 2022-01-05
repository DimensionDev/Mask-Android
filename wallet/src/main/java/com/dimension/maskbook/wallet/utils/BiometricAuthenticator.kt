package com.dimension.maskbook.wallet.utils

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

class BiometricAuthenticator {
    fun canAuthenticate(context: Context): Boolean {
        return BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG).also {
            Log.d("biometric", "can:$it")
        } == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun biometricAuthenticate(
        context: Context,
        title: String = "",
        subtitle: String = "",
        negativeButtonText: String = "Cancel",
        onResult: (Boolean) -> Unit,
        onCanceled: () -> Unit = {}
    ) {
        context.findActivity()?.let {
            val biometricPrompt = BiometricPrompt(it, object :
                BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    Log.d("biometric", "result:${result.cryptoObject?.cipher}")
                    onResult.invoke(true)
                }

                override fun onAuthenticationFailed() {
                    Log.d("biometric", "failed")
                    onResult.invoke(false)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Log.d("biometric", "error:$errorCode, errString:$errString")
                    when (errorCode) {
                        BiometricPrompt.ERROR_CANCELED, BiometricPrompt.ERROR_USER_CANCELED, BiometricPrompt.ERROR_NEGATIVE_BUTTON -> onCanceled.invoke()
                        else -> onResult(false)
                    }
                }
            })
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(BIOMETRIC_STRONG)
                .build()
            biometricPrompt.authenticate(promptInfo)
        } ?: onResult.invoke(false)
    }
}

private fun Context.findActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
