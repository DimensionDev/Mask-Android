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
import com.dimension.maskbook.persona.db.dao.RelationDao
import com.dimension.maskbook.persona.db.sql.buildQueryRelationsSql
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.mock.model.mockDbProfileRecord
import com.dimension.maskbook.persona.mock.model.mockDbRelationRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DbRelationRecordTest : PersonaDatabaseTest() {

    private lateinit var relationDao: RelationDao
    private lateinit var profileDao: ProfileDao

    override fun onCreateDb() {
        super.onCreateDb()
        relationDao = db.relationDao()
        profileDao = db.profileDao()
    }

    @Test
    fun test_repeat_insert() = runTest {
        val relation1 = mockDbRelationRecord(
            personaIdentifier = "person:twitter.com/AAA",
            profileIdentifier = "profile1",
        )
        val relation2 = mockDbRelationRecord(
            personaIdentifier = "person:twitter.com/AAA",
            profileIdentifier = "profile2",
        )
        val relation22 = mockDbRelationRecord(
            personaIdentifier = "person:twitter.com/AAA",
            profileIdentifier = "profile2",
        )

        relationDao.insert(relation1)
        relationDao.insert(relation1)
        relationDao.insert(relation2)
        relationDao.insert(relation22)

        val list = relationDao.findList("person:twitter.com/AAA")
        assertEquals(list.size, 2)
    }

    @Test
    fun test_query_relations_sql() = runTest {
        val sqlProfile1 = mockDbProfileRecord(
            identifier = "sql_profile1",
            nickname = "sql_profile1",
            network = Network.Minds,
        )
        val sqlProfile2 = mockDbProfileRecord(
            identifier = "sql_profile2",
            nickname = "sql_profile2",
        )
        profileDao.insert(sqlProfile1)
        profileDao.insert(sqlProfile2)

        val relation1 = mockDbRelationRecord(
            personaIdentifier = "person:twitter.com/sql1",
            profileIdentifier = "sql_profile1",
        )
        val relation2 = mockDbRelationRecord(
            personaIdentifier = "person:twitter.com/sql2",
            profileIdentifier = "sql_profile2",
        )
        relationDao.insert(relation1)
        relationDao.insert(relation2)

        var query = buildQueryRelationsSql(
            personaIdentifier = "person:twitter.com/sql1"
        )
        var list = relationDao.findListRaw(query)
        assertEquals(list.size, 1)
        assertEquals(list[0].profileIdentifier, "sql_profile1")

        query = buildQueryRelationsSql(
            nameContains = "sql_",
        )
        list = relationDao.findListRaw(query)
        assertEquals(list.size, 2)
        assertTrue { list.any { it.profileIdentifier == "sql_profile1" } }
        assertTrue { list.any { it.profileIdentifier == "sql_profile2" } }

        query = buildQueryRelationsSql(
            network = Network.Minds.value,
        )
        list = relationDao.findListRaw(query)
        assertTrue { list.any { it.profileIdentifier == "sql_profile1" } }
    }
}
