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

import com.dimension.maskbook.common.manager.ImageLoaderManager
import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.migrator.mapper.toDbLinkedProfileRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toDbProfileRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toIndexedDBProfile
import com.dimension.maskbook.persona.db.model.DbLinkedProfileRecord
import com.dimension.maskbook.persona.db.sql.buildQueryProfileSql
import com.dimension.maskbook.persona.db.sql.buildQueryProfilesSql
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.model.indexed.IndexedDBProfile
import com.dimension.maskbook.persona.model.options.AttachProfileOptions
import com.dimension.maskbook.persona.model.options.CreateProfileOptions
import com.dimension.maskbook.persona.model.options.DeleteProfileOptions
import com.dimension.maskbook.persona.model.options.DetachProfileOptions
import com.dimension.maskbook.persona.model.options.QueryAvatarOptions
import com.dimension.maskbook.persona.model.options.QueryProfileOptions
import com.dimension.maskbook.persona.model.options.QueryProfilesOptions
import com.dimension.maskbook.persona.model.options.StoreAvatarOptions
import com.dimension.maskbook.persona.model.options.UpdateProfileOptions

class JsProfileDataSource(
    database: PersonaDatabase,
    private val imageLoaderManager: ImageLoaderManager,
) {

    private val profileDao = database.profileDao()
    private val linkedProfileDao = database.linkedProfileDao()
    private val relationDao = database.relationDao()

    suspend fun createProfile(options: CreateProfileOptions): IndexedDBProfile {
        options.profile.toDbLinkedProfileRecord()?.let {
            linkedProfileDao.insert(it)
        }

        val newProfile = options.profile.toDbProfileRecord()
        profileDao.insert(newProfile)
        return options.profile
    }

    suspend fun queryProfile(options: QueryProfileOptions): IndexedDBProfile? {
        val query = buildQueryProfileSql(
            identifier = options.profileIdentifier,
            network = options.network,
            nameContains = options.nameContains,
        )
        return profileDao.findRaw(query)?.toIndexedDBProfile()
    }

    suspend fun queryProfiles(options: QueryProfilesOptions): List<IndexedDBProfile> {
        val query = buildQueryProfilesSql(
            identifiers = options.profileIdentifiers,
            network = options.network,
            nameContains = options.nameContains,
            pageOptions = options.pageOptions,
        )
        return profileDao.findListRaw(query).map {
            it.toIndexedDBProfile()
        }
    }

    suspend fun updateProfile(options: UpdateProfileOptions): IndexedDBProfile {
        options.profile.toDbLinkedProfileRecord()?.let {
            linkedProfileDao.insert(it)
        }

        val oldProfile = profileDao.find(options.profile.identifier)
        if (oldProfile != null) {
            return options.profile
        }

        if (options.options.createWhenNotExist) {
            val newProfile = options.profile.toDbProfileRecord()
            newProfile.network = Network.withProfileIdentifier(newProfile.identifier)
            profileDao.insert(newProfile)
        }

        return options.profile
    }

    suspend fun deleteProfile(options: DeleteProfileOptions) {
        profileDao.delete(options.profileIdentifier)
        linkedProfileDao.deleteWithProfile(options.profileIdentifier)
        relationDao.deleteWithProfile(options.profileIdentifier)
    }

    suspend fun attachProfile(options: AttachProfileOptions) {
        var linkedProfile = linkedProfileDao.find(
            personaIdentifier = options.personaIdentifier,
            profileIdentifier = options.profileIdentifier,
        )
        if (linkedProfile == null) {
            linkedProfile = DbLinkedProfileRecord(
                personaIdentifier = options.personaIdentifier,
                profileIdentifier = options.profileIdentifier,
                createdAt = System.currentTimeMillis(),
            )
        }
        linkedProfile.state = LinkedProfileDetailsState.Confirmed
        linkedProfile.updatedAt = System.currentTimeMillis()

        linkedProfileDao.insert(linkedProfile)
    }

    suspend fun detachProfile(options: DetachProfileOptions) {
        linkedProfileDao.deleteWithProfile(options.profileIdentifier)
    }

    suspend fun queryAvatar(options: QueryAvatarOptions): String? {
        return profileDao.find(options.profileIdentifier)?.avatar
            ?.let { imageLoaderManager.convertUrlToBase64(it) }
    }

    suspend fun storeAvatar(options: StoreAvatarOptions) {
        profileDao.updateAvatar(options.profileIdentifier, options.avatar)
    }
}
