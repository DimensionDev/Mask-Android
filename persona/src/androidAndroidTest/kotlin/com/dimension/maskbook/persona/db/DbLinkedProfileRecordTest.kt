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
import com.dimension.maskbook.persona.db.dao.LinkedProfileDao
import com.dimension.maskbook.persona.db.dao.PersonaDao
import com.dimension.maskbook.persona.mock.model.mockDbLinkedProfileRecord
import com.dimension.maskbook.persona.mock.model.mockDbPersonaRecord
import kotlin.test.Test
import kotlin.test.assertEquals

class DbLinkedProfileRecordTest : PersonaDatabaseTest() {

    private lateinit var linkedProfileDao: LinkedProfileDao
    private lateinit var personaDao: PersonaDao

    override fun onCreateDb() {
        super.onCreateDb()
        linkedProfileDao = db.linkedProfileDao()
        personaDao = db.personaDao()
    }

    @Test
    fun test_repeat_insert() = runTest {
        val relation1 = mockDbLinkedProfileRecord(
            personaIdentifier = "person:twitter.com/AAA",
            profileIdentifier = "profile1",
        )
        val relation2 = mockDbLinkedProfileRecord(
            personaIdentifier = "person:twitter.com/AAA",
            profileIdentifier = "profile2",
        )
        val relation22 = mockDbLinkedProfileRecord(
            personaIdentifier = "person:twitter.com/AAA",
            profileIdentifier = "profile2",
        )

        linkedProfileDao.insert(relation1)
        linkedProfileDao.insert(relation1)
        linkedProfileDao.insert(relation2)
        linkedProfileDao.insert(relation22)

        val list = linkedProfileDao.findList("person:twitter.com/AAA")
        assertEquals(list.size, 2)
    }

    @Test
    fun test_query_links() = runTest {
        val personaRemote = mockDbPersonaRecord(
            identifier = "query_link_remote_user",
            privateKey = null,
        )
        val personaLocal = mockDbPersonaRecord(
            identifier = "query_link_local_user",
        )
        personaDao.insert(personaRemote)
        personaDao.insert(personaLocal)

        val link1 = mockDbLinkedProfileRecord(
            personaIdentifier = "person:twitter.com/query_link_local_user",
            profileIdentifier = "query_link1",
        )
        val link2 = mockDbLinkedProfileRecord(
            personaIdentifier = "person:twitter.com/query_link_remote_user",
            profileIdentifier = "query_link1",
        )
        linkedProfileDao.insert(link1)
        linkedProfileDao.insert(link2)

        val list = linkedProfileDao.findListWithProfile("query_link1")
        assertEquals(list.size, 2)
        assertEquals(list[0].personaIdentifier, "person:twitter.com/query_link_local_user")
    }
}
