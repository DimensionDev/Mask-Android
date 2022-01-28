/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.dimension.maskbook.wallet.R

class BiometricAuthenticator {
    fun canAuthenticate(context: Context): Boolean {
        return BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun biometricAuthenticate(
        context: Context,
        title: Int,
        subTitle: Int = -1,
        negativeButtonText: Int = R.string.common_controls_cancel,
        onSuccess: () -> Unit,
        onFailed: (errString: String) -> Unit = {},
        onCanceled: () -> Unit = {}
    ) {
        context.findActivity()?.let {
            val biometricPrompt = BiometricPrompt(
                it,
                object :
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
                }
            )
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.resources.getText(title))
                .setSubtitle(if (subTitle == -1) null else context.resources.getText(subTitle))
                .setNegativeButtonText(context.resources.getText(negativeButtonText))
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
