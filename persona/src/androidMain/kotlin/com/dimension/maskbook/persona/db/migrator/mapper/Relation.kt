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

import com.dimension.maskbook.persona.db.model.DbRelationRecord
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.model.indexed.IndexedDBRelation

fun IndexedDBRelation.toDbRelationRecord(): DbRelationRecord {
    return DbRelationRecord(
        personaIdentifier = personaIdentifier,
        profileIdentifier = profileIdentifier,
        favor = favor == 1,
        updatedAt = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis(),
    )
}

fun DbRelationRecord.toIndexedDBRelation(): IndexedDBRelation {
    return IndexedDBRelation(
        personaIdentifier = personaIdentifier,
        profileIdentifier = profileIdentifier,
        favor = if (favor) 1 else 0,
        network = Network.withProfileIdentifier(profileIdentifier)?.value.orEmpty(),
    )
}
