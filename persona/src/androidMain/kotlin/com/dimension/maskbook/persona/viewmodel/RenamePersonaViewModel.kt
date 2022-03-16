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
import com.dimension.maskbook.persona.repository.DbPersonaRepository
import com.dimension.maskbook.persona.repository.IPersonaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RenamePersonaViewModel(
    private val repository: IPersonaRepository,
    private val personaRepository: DbPersonaRepository,
    private val personaId: String,
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateIn(viewModelScope)

    init {
        viewModelScope.launch {
            _name.value = personaRepository.getPersona(personaId)?.name.orEmpty()
        }
    }

    fun setName(value: String) {
        _name.value = value
    }

    fun confirm() {
        repository.updatePersona(personaId, _name.value)
    }
}
