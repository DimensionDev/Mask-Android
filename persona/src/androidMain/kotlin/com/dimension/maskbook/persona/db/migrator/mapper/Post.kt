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

import com.dimension.maskbook.persona.db.model.DbPostRecord
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.model.indexed.IndexedDBPost

fun IndexedDBPost.toDbPostRecord(): DbPostRecord {
    return DbPostRecord(
        identifier = identifier,
        encryptBy = encryptBy,
        postNetwork = Network.withProfileIdentifier(identifier) ?: Network.Twitter,
        postUserId = postBy,
        postCryptoKeyRaw = postCryptoKey,
        url = url,
        summary = summary,
        recipientsRaw = recipients,
        interestedMetaRaw = interestedMeta,
        foundAt = foundAt,
    )
}

fun DbPostRecord.toIndexedDBPost(): IndexedDBPost {
    return IndexedDBPost(
        postBy = postUserId,
        identifier = identifier,
        postCryptoKey = postCryptoKeyRaw,
        recipients = recipientsRaw,
        foundAt = foundAt,
        encryptBy = encryptBy,
        url = url,
        summary = summary,
        interestedMeta = interestedMetaRaw,
    )
}
