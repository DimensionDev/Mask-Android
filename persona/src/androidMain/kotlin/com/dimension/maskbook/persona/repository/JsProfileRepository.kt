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

import androidx.sqlite.db.SimpleSQLiteQuery
import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.dao.ProfileDao
import com.dimension.maskbook.persona.db.model.DbLinkedProfileRecord
import com.dimension.maskbook.persona.db.model.DbProfileRecord
import com.dimension.maskbook.persona.model.LinkedProfileDetailsState
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

    suspend fun createProfile(options: CreateProfileOptions): DbProfileRecord? {
        val newProfile = options.profile
        newProfile.createdAt = System.currentTimeMillis()
        newProfile.updatedAt = System.currentTimeMillis()
        return profileDao.addWithResult(options.profile)
    }

    suspend fun queryProfile(options: QueryProfileOptions): DbProfileRecord? {
        val query = buildString {
            append("SELECT * FROM DbProfileRecord WHERE identifier = ${options.identifier} ")
            val whereSql = buildWhereSql(
                network = options.network,
                nameContains = options.nameContains,
            )
            if (whereSql.isNotEmpty()) {
                append("$whereSql ")
            }
            append("LIMIT 1")
        }
        return profileDao.findRaw(SimpleSQLiteQuery(query))
    }

    suspend fun queryProfiles(options: QueryProfilesOptions): List<DbProfileRecord> {
        val query = buildString {
            append("SELECT * FROM DbProfileRecord ")
            val whereSql = buildWhereSql(
                identifiers = options.identifiers,
                network = options.network,
                nameContains = options.nameContains,
            )
            if (whereSql.isNotEmpty()) {
                append("WHERE $whereSql ")
            }
            if (options.pageOption != null) {
                append("LIMIT ${options.pageOption.limitStart} OFFSET ${options.pageOption.limitOffset}")
            }
        }
        return profileDao.findListRaw(SimpleSQLiteQuery(query))
    }

    suspend fun updateProfile(options: UpdateProfileOptions): DbProfileRecord? {
        val oldProfile = profileDao.find(options.profile.identifier)
        val newProfile = options.profile

        if (oldProfile == null) {
            return if (options.options.createWhenNotExist) {
                newProfile.createdAt = System.currentTimeMillis()
                newProfile.updatedAt = System.currentTimeMillis()
                profileDao.addWithResult(newProfile)
            } else null
        }

        newProfile.createdAt = oldProfile.createdAt
        newProfile.updatedAt = System.currentTimeMillis()
        return profileDao.addWithResult(newProfile)
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
        linkedProfile.state = LinkedProfileDetailsState.Pending
        linkedProfile.updatedAt = System.currentTimeMillis()

        linkedProfileDao.add(linkedProfile)
    }

    suspend fun detachProfile(options: DetachProfileOptions) {
        linkedProfileDao.delete(
            profileIdentifier = options.profileIdentifier
        )
    }
}

private suspend fun ProfileDao.addWithResult(profile: DbProfileRecord): DbProfileRecord {
    add(profile) ; return profile
}
