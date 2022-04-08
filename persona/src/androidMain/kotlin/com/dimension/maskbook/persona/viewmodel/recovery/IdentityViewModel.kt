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
package com.dimension.maskbook.persona.viewmodel.recovery

import com.dimension.maskbook.common.ext.Validator
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.wallet.export.WalletServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class IdentityViewModel(
    private val personaServices: PersonaServices,
    private val walletServices: WalletServices,
    private val name: String
) : ViewModel() {

    private val _identity = MutableStateFlow("")
    val identity = _identity.asStateIn(viewModelScope)

    val canConfirm = _identity.map {
        Validator.isMnemonic(it.trim()) && walletServices.validateMnemonic(it.trim())
    }.asStateIn(viewModelScope, false)

    fun setIdentity(text: String) {
        _identity.value = text
    }

    suspend fun confirm(): Result<Unit> {
        return runCatching {
            personaServices.createPersonaFromMnemonic(_identity.value.trim().split(" "), name)
        }
    }
}
