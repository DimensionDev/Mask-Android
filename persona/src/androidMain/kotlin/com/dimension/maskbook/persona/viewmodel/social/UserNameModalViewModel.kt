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
package com.dimension.maskbook.persona.viewmodel.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.persona.export.model.ConnectAccountData
import com.dimension.maskbook.persona.repository.IPersonaRepository
import kotlinx.coroutines.flow.MutableStateFlow

class UserNameModalViewModel(
    private val personaRepository: IPersonaRepository,
    private val data: ConnectAccountData,
) : ViewModel() {
    private val _userName = MutableStateFlow(data.profile.userId)
    val userName = _userName.asStateIn(viewModelScope, data.profile.userId)
    fun setUserName(value: String) {
        _userName.value = value
    }

    fun done(name: String) {
        personaRepository.finishConnectingProcess(data.profile.copy(userId = name), data.personaId)
    }
}
