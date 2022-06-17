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
package com.dimension.maskbook.persona.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.of
import com.dimension.maskbook.common.ext.onFinished
import com.dimension.maskbook.persona.repository.IPersonaRepository
import com.dimension.maskbook.setting.export.SettingServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PersonaLogoutViewModel(
    private val repository: IPersonaRepository,
    settingServices: SettingServices,
) : ViewModel() {

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateIn(viewModelScope)

    private val _done = MutableStateFlow(false)
    val done = _done.asStateIn(viewModelScope)

    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope)

    val confirmPassword = combine(
        settingServices.backupPassword,
        password
    ) { currentPassword, password ->
        currentPassword == password
    }.asStateIn(viewModelScope, false)

    fun setPassword(password: String) {
        _password.value = password
    }

    fun logout() = viewModelScope.launch {
        _loadingState.value = true
        Result.of {
            repository.logout()
        }.onFailure {
            it.printStackTrace()
        }.onFinished {
            _done.value = true
            _loadingState.value = false
        }
    }
}
