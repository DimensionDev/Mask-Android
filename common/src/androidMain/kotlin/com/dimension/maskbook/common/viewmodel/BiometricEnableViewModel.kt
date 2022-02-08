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
import com.dimension.maskbook.common.util.BiometricAuthenticator
import com.dimension.maskbook.setting.export.SettingServices

class BiometricEnableViewModel(
    private val biometricAuthenticator: BiometricAuthenticator,
    private val repository: SettingServices,
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
