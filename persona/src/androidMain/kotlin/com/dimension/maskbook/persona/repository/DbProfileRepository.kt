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
package com.dimension.maskbook.persona.repository

import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.model.DbProfileRecord
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.SocialData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DbProfileRepository(database: PersonaDatabase) {

    private val profileDao = database.profileDao()
    
    fun getSocialListFlow(
        personaIdentifier: String,
    ): Flow<List<SocialData>> {
        return profileDao.getListWithPersonaFlow(personaIdentifier).map { list ->
            list.map { profile ->
                profile.toSocialData()
            }
        }
    }

    fun getSocialListFlow(
        personaIdentifier: String,
        network: Network,
    ): Flow<List<SocialData>> {
        return profileDao.getListWithPersonaFlow(personaIdentifier, network).map { list ->
            list.map { profile ->
                profile.toSocialData()
            }
        }
    }
}

private fun DbProfileRecord.toSocialData(): SocialData {
    return SocialData(
        id = identifier,
        name = nickname.orEmpty(),
        avatar = avatar.orEmpty(),
        network = Network.Facebook,
        personaId = null,
        linkedPersona = false,
        // personaId = profile.linkedPersona?.persona?.identifier.orEmpty(),
        // linkedPersona = profile.linkedPersona != null,
    )
}