/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
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
