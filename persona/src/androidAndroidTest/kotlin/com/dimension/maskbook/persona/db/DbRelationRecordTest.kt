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
import com.dimension.maskbook.persona.db.dao.RelationDao
import com.dimension.maskbook.persona.mock.model.mockDbRelationRecord
import kotlin.test.Test
import kotlin.test.assertEquals

class DbRelationRecordTest : PersonaDatabaseTest() {

    private lateinit var relationDao: RelationDao

    override fun onCreateDb() {
        super.onCreateDb()
        relationDao = db.relationDao()
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
}
