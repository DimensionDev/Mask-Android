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
import com.dimension.maskbook.persona.db.dao.PersonaDao
import com.dimension.maskbook.persona.db.sql.buildQueryPersonasSql
import com.dimension.maskbook.persona.mock.model.mockDbPersonaRecord
import com.dimension.maskbook.persona.model.options.PageOptions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DbPersonaRecordTest : PersonaDatabaseTest() {

    private lateinit var personaDao: PersonaDao

    override fun onCreateDb() {
        super.onCreateDb()
        personaDao = db.personaDao()
    }

    @Test
    fun test_insert_find_delete() = runTest {
        val persona1 = mockDbPersonaRecord(
            identifier = "person:twitter.com/AAA",
            nickname = "AAA",
        )
        personaDao.insert(persona1)
        assertNotNull(personaDao.find("person:twitter.com/AAA"))
        assertNull(personaDao.find("person:twitter.com/BBB"))

        personaDao.delete(persona1.identifier)
        assertNull(personaDao.find("person:twitter.com/AAA"))
    }

    @Test
    fun test_sql_query() = runTest {
        val persona1 = mockDbPersonaRecord(
            identifier = "person:twitter.com/findRaw1",
            nickname = "findRaw1",
            hasLogout = false,
            privateKey = null,
        )
        val persona2 = mockDbPersonaRecord(
            identifier = "person:twitter.com/findRaw2",
            nickname = "findRaw2",
            hasLogout = true,
            privateKey = null,
        )
        val persona3 = mockDbPersonaRecord(
            identifier = "person:twitter.com/findRaw3",
            nickname = "findRaw3",
            hasLogout = false,
        )
        val persona4 = mockDbPersonaRecord(
            identifier = "person:twitter.com/findRaw4",
            nickname = "findRaw4",
            hasLogout = false,
            privateKey = null,
        )
        personaDao.insert(persona1)
        personaDao.insert(persona2)
        personaDao.insert(persona3)
        personaDao.insert(persona4)

        var query = buildQueryPersonasSql(
            includeLogout = false,
            pageOptions = PageOptions(0, 2)
        )
        var list = personaDao.findListRaw(query)
        assertEquals(list.size, 2)
        assert(list.any { it.persona == persona1 })
        assert(list.any { it.persona == persona3 })

        query = buildQueryPersonasSql(
            includeLogout = false,
            pageOptions = PageOptions(1, 2)
        )
        list = personaDao.findListRaw(query)
        assertEquals(list.size, 1)
        assert(list.any { it.persona == persona4 })

        query = buildQueryPersonasSql(
            hasPrivateKey = true,
            pageOptions = PageOptions(0, 2)
        )
        list = personaDao.findListRaw(query)
        assertEquals(list.size, 1)
        assert(list.any { it.persona == persona3 })

        query = buildQueryPersonasSql(
            identifiers = listOf(
                "person:twitter.com/findRaw1",
                "person:twitter.com/findRaw3",
            ),
        )
        list = personaDao.findListRaw(query)
        assertEquals(list.size, 2)
        assert(list.any { it.persona == persona1 })
        assert(list.any { it.persona == persona3 })
    }

    @Test
    fun test_find_mnemonic() = runTest {
        val persona1 = mockDbPersonaRecord(
            identifier = "person:twitter.com/findMnemonic1",
            nickname = "findMnemonic1",
            mnemonic = "findMnemonic1",
            hasLogout = false,
            privateKey = null,
        )
        personaDao.insert(persona1)

        val list = personaDao.findList()
        assertNotNull(list.find { it.mnemonic == "findMnemonic1" })
    }
}
