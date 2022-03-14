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
package com.dimension.maskbook.persona.db.migrator

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.persona.db.base.PersonaDatabaseTest
import com.dimension.maskbook.persona.db.migrator.model.IndexedDBAllRecord
import com.dimension.maskbook.persona.export.model.Network
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IndexedDBDataMigratorTest : PersonaDatabaseTest() {

    private val recordsJson = """
        {
            "profiles": [
                {
                    "updatedAt": 1646830937388,
                    "identifier": "person:twitter.com/AAA",
                    "nickname": "AAA",
                    "createdAt": 1646830937388,
                    "linkedPersona": null,
                    "localKey": null
                },
                {
                    "updatedAt": 1646709286152,
                    "identifier": "person:twitter.com/BBB",
                    "nickname": "BBB",
                    "createdAt": 1646709286152,
                    "linkedPersona": null,
                    "localKey": null
                }
            ],
            "personas": [
                {
                    "updatedAt": 1646386534519,
                    "privateKey": {
                        "key_ops": [
                            "derive1",
                            "derive2"
                        ],
                        "d": "aaa",
                        "x": "bbb",
                        "y": "ccc",
                        "crv": "crv",
                        "ext": true,
                        "kty": "kty"
                    },
                    "uninitialized": null,
                    "identifier": "ec_key:aaa/bbb",
                    "mnemonic": {
                        "words": "this is words",
                        "parameter": {
                            "withPassword": true,
                            "path": "m/44'/60'/0'/0/0"
                        }
                    },
                    "linkedProfiles": {
                        "person:twitter.com/AAA": {
                            "connectionConfirmState": "confirmed"
                        }
                    },
                    "hasLogout": false,
                    "nickname": "seiko",
                    "createdAt": 1646386534519,
                    "publicKey": {
                        "key_ops": [
                            "derive1",
                            "derive2"
                        ],
                        "x": "aaa",
                        "y": "bbb",
                        "crv": "crv",
                        "ext": true,
                        "kty": "kty"
                    },
                    "localKey": {
                        "key_ops": [
                            "encrypt",
                            "decrypt"
                        ],
                        "k": "aaa",
                        "alg": "bbb",
                        "ext": true,
                        "kty": "ccc"
                    }
                }
            ],
            relations: [
                {
                    "linked": "ec_key:aaa/bbb",
                    "profile": "person:twitter.com/CCC",
                    "favor": 1,
                    "network": "twitter.com"
                }
            ]
        }
    """.trimIndent()

    @Test
    fun test_indexedDb_migrator() = runTest {
        val records: IndexedDBAllRecord = recordsJson.decodeJson()
        IndexedDBDataMigrator(db).migrate(records)

        val personaDao = db.personaDao()
        val persona = personaDao.find("ec_key:aaa/bbb")
        assertNotNull(persona)
        assertNotNull(persona.privateKey)
        assertEquals(persona.withPassword, true)

        val profileDao = db.profileDao()
        val profile = profileDao.find("person:twitter.com/AAA")
        assertNotNull(profile)
        assertEquals(profile.network, Network.Twitter)

        val linkedProfileDao = db.linkedProfileDao()
        val linkedProfile = linkedProfileDao.find("ec_key:aaa/bbb", "person:twitter.com/AAA")
        assertNotNull(linkedProfile)

        val relationDao = db.relationDao()
        val relation = relationDao.find("ec_key:aaa/bbb", "person:twitter.com/CCC")
        assertNotNull(relation)
        assertTrue(relation.favor)
    }
}
