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
package com.dimension.maskbook.persona.db

import com.dimension.maskbook.persona.db.base.PersonaDatabaseTest
import com.dimension.maskbook.persona.db.dao.ProfileDao
import com.dimension.maskbook.persona.db.sql.buildQueryProfileSql
import com.dimension.maskbook.persona.db.sql.buildQueryProfilesSql
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.mock.model.mockDbProfileRecord
import kotlin.test.Test
import kotlin.test.assertTrue

class DbProfileRecordTest : PersonaDatabaseTest() {

    private lateinit var profileDao: ProfileDao

    override fun onCreateDb() {
        super.onCreateDb()
        profileDao = db.profileDao()
    }

    @Test
    fun test_query_profile_sql() = runTest {
        val profile1 = mockDbProfileRecord(
            identifier = "test_sql_profile1",
            nickname = "test_sql_profile1",
        )
        profileDao.insert(profile1)

        val query = buildQueryProfileSql(
            identifier = "test_sql_profile1",
        )
        val list = profileDao.findListRaw(query)
        assertTrue { list.any { it.profile.identifier == profile1.identifier } }
    }

    @Test
    fun test_query_profiles_sql() = runTest {
        val profile1 = mockDbProfileRecord(
            identifier = "test_sql_profile11",
            nickname = "test_sql_profile11",
        )
        val profile2 = mockDbProfileRecord(
            identifier = "test_sql_profile12",
            nickname = "test_sql_profile12",
            network = Network.Minds,
        )
        val profile3 = mockDbProfileRecord(
            identifier = "test_sql_profile13",
            nickname = "test_sql_profile13",
        )
        profileDao.insert(profile1)
        profileDao.insert(profile2)
        profileDao.insert(profile3)

        var query = buildQueryProfilesSql(
            identifiers = listOf("test_sql_profile11", "test_sql_profile12")
        )
        var list = profileDao.findListRaw(query)
        assertTrue { list.any { it.profile.identifier == profile1.identifier } }
        assertTrue { list.any { it.profile.identifier == profile2.identifier } }

        query = buildQueryProfilesSql(
            network = Network.Minds.value,
        )
        list = profileDao.findListRaw(query)
        assertTrue { list.any { it.profile.identifier == profile2.identifier } }

        query = buildQueryProfilesSql(
            nameContains = "profile13"
        )
        list = profileDao.findListRaw(query)
        assertTrue { list.any { it.profile.identifier == profile3.identifier } }
    }
}
