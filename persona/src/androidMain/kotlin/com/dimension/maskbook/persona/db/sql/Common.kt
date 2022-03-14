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
package com.dimension.maskbook.persona.db.sql

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.dimension.maskbook.persona.model.options.PageOptions

internal fun String.asSqlQuery(): SupportSQLiteQuery {
    Log.i("JSMethodV2", this)
    return SimpleSQLiteQuery(this)
}

internal fun PageOptions.asLimitSql(): String {
    return "LIMIT $pageOffset OFFSET ${pageSize * pageOffset}"
}

internal fun buildWhereSql(
    identifiers: List<String>? = null,
    hasPrivateKey: Boolean? = null,
    includeLogout: Boolean? = null,
    nameContains: String? = null,
    initialized: Boolean? = null,
    network: String? = null,
    favor: Boolean? = null,
): String {
    return listOfNotNull(
        if (!identifiers.isNullOrEmpty()) {
            "identifier in (${identifiers.joinToString(",") { "'$it'" }})"
        } else null,
        if (hasPrivateKey != null) {
            "privateKey IS NOT NULL"
        } else null,
        if (includeLogout != null && includeLogout == false) {
            "hasLogout = false"
        } else null,
        if (!nameContains.isNullOrEmpty()) {
            "nickname LIKE '%$nameContains%'"
        } else null,
        if (initialized != null) {
            "initialized = $initialized"
        } else null,
        if (!network.isNullOrEmpty()) {
            "network = '$network'"
        } else null,
        if (favor != null) {
            "favor = $favor"
        } else null,
    ).joinToString(separator = " AND ")
}
