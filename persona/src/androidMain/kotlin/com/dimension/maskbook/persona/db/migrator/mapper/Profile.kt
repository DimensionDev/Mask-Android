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
package com.dimension.maskbook.persona.db.migrator.mapper

import com.dimension.maskbook.persona.db.model.DbLinkedProfileRecord
import com.dimension.maskbook.persona.db.model.DbProfileRecord
import com.dimension.maskbook.persona.db.model.ProfileWithLinkedProfile
import com.dimension.maskbook.persona.export.model.IndexedDBProfile
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.persona.export.model.Network

fun IndexedDBProfile.toDbProfileRecord(): DbProfileRecord {
    return DbProfileRecord(
        identifier = identifier,
        nickname = nickname,
        network = Network.withProfileIdentifier(identifier),
        updatedAt = updatedAt,
        createdAt = createdAt,
    )
}

fun IndexedDBProfile.toDbLinkedProfileRecord(): DbLinkedProfileRecord? {
    if (linkedPersona.isNullOrEmpty()) return null
    return DbLinkedProfileRecord(
        personaIdentifier = linkedPersona.orEmpty(),
        profileIdentifier = identifier,
        state = LinkedProfileDetailsState.Confirmed,
    )
}

fun ProfileWithLinkedProfile.toIndexedDBProfile(): IndexedDBProfile {
    val link = links.asSequence()
        .sortedBy { it.localKey == null }
        .firstOrNull { it.state.isLinked() }
    return IndexedDBProfile(
        identifier = profile.identifier,
        nickname = profile.nickname,
        linkedPersona = link?.personaIdentifier,
        localKey = link?.localKey,
        updatedAt = profile.updatedAt,
        createdAt = profile.createdAt,
    )
}
