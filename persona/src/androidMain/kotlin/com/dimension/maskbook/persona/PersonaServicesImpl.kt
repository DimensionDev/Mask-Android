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
package com.dimension.maskbook.persona

import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.persona.export.model.PersonaData
import com.dimension.maskbook.persona.export.model.SocialProfile
import com.dimension.maskbook.persona.repository.IPersonaRepository
import kotlinx.coroutines.flow.Flow

class PersonaServicesImpl(
    private val personaRepository: IPersonaRepository,
) : PersonaServices {

    override val currentPersona: Flow<PersonaData?>
        get() = personaRepository.currentPersona

    override suspend fun hasPersona(): Boolean {
        return personaRepository.hasPersona()
    }

    override fun updateCurrentPersona(value: String) {
        personaRepository.updateCurrentPersona(value)
    }

    override suspend fun createPersonaFromMnemonic(value: List<String>, name: String) {
        personaRepository.createPersonaFromMnemonic(value, name)
    }

    override fun createPersonaFromPrivateKey(value: String) {
        personaRepository.createPersonaFromPrivateKey(value)
    }

    override fun finishConnectingProcess(profile: SocialProfile, personaId: String) {
        personaRepository.finishConnectingProcess(profile, personaId)
    }

    override fun cancelConnectingProcess() {
        personaRepository.cancelConnectingProcess()
    }

    override fun saveEmailForCurrentPersona(value: String) {
        personaRepository.saveEmailForCurrentPersona(value)
    }

    override fun savePhoneForCurrentPersona(value: String) {
        personaRepository.savePhoneForCurrentPersona(value)
    }

    // override suspend fun refreshPersonaData() {
    //     personaRepository.refreshPersona()
    // }

    // override suspend fun ensurePersonaDataLoaded() {
    //     personaRepository.ensurePersonaDataLoaded()
    // }
}
