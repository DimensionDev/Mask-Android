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
import com.dimension.maskbook.persona.db.dao.ProfileDao
import com.dimension.maskbook.persona.db.migrator.IndexedDBDataMigrator
import com.dimension.maskbook.persona.db.model.DbLinkedProfileRecord
import com.dimension.maskbook.persona.db.model.DbProfileRecord
import com.dimension.maskbook.persona.db.sql.asSqlQuery
import com.dimension.maskbook.persona.db.sql.buildQueryProfileSql
import com.dimension.maskbook.persona.db.sql.buildQueryProfilesSql
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.model.options.AttachProfileOptions
import com.dimension.maskbook.persona.model.options.CreateProfileOptions
import com.dimension.maskbook.persona.model.options.DeleteProfileOptions
import com.dimension.maskbook.persona.model.options.DetachProfileOptions
import com.dimension.maskbook.persona.model.options.QueryProfileOptions
import com.dimension.maskbook.persona.model.options.QueryProfilesOptions
import com.dimension.maskbook.persona.model.options.UpdateProfileOptions

class JsProfileRepository(database: PersonaDatabase) {

    private val profileDao = database.profileDao()
    private val linkedProfileDao = database.linkedProfileDao()

    suspend fun createProfile(options: CreateProfileOptions): DbProfileRecord {
        val newProfile = IndexedDBDataMigrator.mapToDbProfileRecord(options.profile)
        return profileDao.addWithResult(newProfile)
    }

    suspend fun queryProfile(options: QueryProfileOptions): DbProfileRecord? {
        val query = buildQueryProfileSql(
            identifier = options.identifier,
            network = options.network,
            nameContains = options.nameContains,
        )
        return profileDao.findRaw(query.asSqlQuery())
    }

    suspend fun queryProfiles(options: QueryProfilesOptions): List<DbProfileRecord> {
        val query = buildQueryProfilesSql(
            identifiers = options.identifiers,
            network = options.network,
            nameContains = options.nameContains,
            pageOptions = options.pageOptions,
        )
        return profileDao.findListRaw(query.asSqlQuery())
    }

    suspend fun updateProfile(options: UpdateProfileOptions): DbProfileRecord? {
        val oldProfile = profileDao.find(options.profile.identifier)
        val newProfile = options.profile
        if (oldProfile != null) {
            return oldProfile
        }

        if (options.options.createWhenNotExist) {
            newProfile.network = Network.withProfileIdentifier(newProfile.identifier)
            return profileDao.addWithResult(newProfile)
        }

        return null
    }

    suspend fun deleteProfile(options: DeleteProfileOptions) {
        profileDao.delete(options.identifier)
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
        linkedProfileDao.delete(
            profileIdentifier = options.profileIdentifier,
        )
    }
}

private suspend fun ProfileDao.addWithResult(profile: DbProfileRecord): DbProfileRecord {
    insert(profile) ; return profile
}
