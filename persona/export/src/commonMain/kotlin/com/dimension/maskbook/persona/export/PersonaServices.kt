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
package com.dimension.maskbook.persona.export

import com.dimension.maskbook.persona.export.model.IndexedDBPersona
import com.dimension.maskbook.persona.export.model.IndexedDBPost
import com.dimension.maskbook.persona.export.model.IndexedDBProfile
import com.dimension.maskbook.persona.export.model.IndexedDBRelation
import com.dimension.maskbook.persona.export.model.PersonaData
import kotlinx.coroutines.flow.Flow

interface PersonaServices {
    val currentPersona: Flow<PersonaData?>
    suspend fun hasPersona(): Boolean
    fun updateCurrentPersona(value: String)
    suspend fun createPersonaFromMnemonic(value: List<String>, name: String)
    suspend fun createPersonaFromPrivateKey(value: String, name: String)
    fun connectProfile(personaId: String, profileId: String)
    fun saveEmailForCurrentPersona(value: String)
    fun savePhoneForCurrentPersona(value: String)
    suspend fun createPersonaBackup(hasPrivateKeyOnly: Boolean): List<IndexedDBPersona>
    suspend fun restorePersonaBackup(persona: List<IndexedDBPersona>)
    suspend fun createProfileBackup(): List<IndexedDBProfile>
    suspend fun restoreProfileBackup(profile: List<IndexedDBProfile>)
    suspend fun createRelationsBackup(): List<IndexedDBRelation>
    suspend fun restoreRelationBackup(relation: List<IndexedDBRelation>)
    suspend fun createPostsBackup(): List<IndexedDBPost>
    suspend fun restorePostBackup(post: List<IndexedDBPost>)
}
