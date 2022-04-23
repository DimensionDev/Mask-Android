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

import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.wallet.export.WalletServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class PrivateKeyViewModel(
    private val personaServices: PersonaServices,
    private val walletServices: WalletServices,
) : ViewModel() {

    private val _privateKey = MutableStateFlow("")
    val privateKey = _privateKey.asStateIn(viewModelScope)

    val canConfirm = _privateKey.map {
        walletServices.validatePrivateKey(it.trim())
    }.asStateIn(viewModelScope, false)

    fun setPrivateKey(text: String) {
        _privateKey.value = text
    }

    suspend fun confirm(nickname: String = "persona1"): Result<Unit> {
        return runCatching {
            personaServices.createPersonaFromPrivateKey(_privateKey.value.trim(), name = nickname)
        }
    }
}
