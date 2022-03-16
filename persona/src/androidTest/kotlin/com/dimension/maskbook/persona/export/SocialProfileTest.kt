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

import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.SocialProfile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SocialProfileTest {

    @Test
    fun test_parse_personaIdentifier() {
        val socialProfile = SocialProfile.parse("person:twitter.com/AA_BB")
        assertNotNull(socialProfile)
        assertEquals(socialProfile.network, Network.Twitter)
        assertEquals(socialProfile.userId, "AA_BB")
    }
}
