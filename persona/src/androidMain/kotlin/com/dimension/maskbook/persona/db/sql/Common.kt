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

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.dimension.maskbook.persona.model.options.PageOptions

internal fun buildQuerySql(
    dbName: String,
    identifier: String? = null,
    personaIdentifier: String? = null,
    profileIdentifier: String? = null,
    identifiers: List<String>? = null,
    hasPrivateKey: Boolean? = null,
    includeLogout: Boolean? = null,
    nameContains: String? = null,
    initialized: Boolean? = null,
    network: String? = null,
    favor: Boolean? = null,
    encryptBy: String? = null,
    userIds: List<String>? = null,
    pageOptions: PageOptions? = PageOptions.ONE,
): SupportSQLiteQuery {
    val query = StringBuilder("SELECT * FROM $dbName")
    val args = mutableListOf<Any>()
    buildWhereSql(
        args = args,
        identifier = identifier,
        personaIdentifier = personaIdentifier,
        profileIdentifier = profileIdentifier,
        identifiers = identifiers,
        hasPrivateKey = hasPrivateKey,
        includeLogout = includeLogout,
        nameContains = nameContains,
        initialized = initialized,
        network = network,
        favor = favor,
        encryptBy = encryptBy,
        userIds = userIds,
    )?.let {
        query.append(" WHERE $it")
    }
    if (pageOptions === PageOptions.ONE) {
        query.append(" LIMIT 1")
    } else if (pageOptions != null) {
        query.append(" ${pageOptions.asLimitSql()}")
    }
    return SimpleSQLiteQuery(query.toString(), args.toTypedArray())
}

private fun buildWhereSql(
    args: MutableList<Any>,
    identifier: String? = null,
    personaIdentifier: String? = null,
    profileIdentifier: String? = null,
    identifiers: List<String>? = null,
    hasPrivateKey: Boolean? = null,
    includeLogout: Boolean? = null,
    nameContains: String? = null,
    initialized: Boolean? = null,
    network: String? = null,
    favor: Boolean? = null,
    encryptBy: String? = null,
    userIds: List<String>? = null,
): String? {
    return listOfNotNull(
        if (!identifier.isNullOrEmpty()) {
            args.add(identifier)
            "identifier = :identifier"
        } else null,
        if (!personaIdentifier.isNullOrEmpty()) {
            args.add(personaIdentifier)
            "personaIdentifier = :personaIdentifier"
        } else null,
        if (!profileIdentifier.isNullOrEmpty()) {
            args.add(profileIdentifier)
            "identifier in " +
                "(SELECT personaIdentifier FROM DbLinkedProfileRecord WHERE " +
                "profileIdentifier = :profileIdentifier " +
                "LIMIT 1) "
        } else null,
        if (!identifiers.isNullOrEmpty()) {
            "identifier in (${identifiers.joinToString(",") { "'$it'" }})"
        } else null,
        if (hasPrivateKey != null) {
            "privateKeyRaw IS NOT NULL"
        } else null,
        if (includeLogout != null && includeLogout == false) {
            args.add(false)
            "hasLogout = :hasLogout"
        } else null,
        if (!nameContains.isNullOrEmpty()) {
            args.add("%$nameContains%")
            "nickname LIKE :nameContains"
        } else null,
        if (initialized != null) {
            args.add(initialized)
            "initialized = :initialized"
        } else null,
        if (!network.isNullOrEmpty()) {
            args.add(network)
            "network = :network"
        } else null,
        if (favor != null) {
            args.add(favor)
            "favor = :favor"
        } else null,
        if (!encryptBy.isNullOrEmpty()) {
            args.add(encryptBy)
            "encryptBy = :encryptBy"
        } else null,
        if (!userIds.isNullOrEmpty()) {
            "postUserId in (${userIds.joinToString(",") { "'$it'" }})"
        } else null,
    ).joinToString(separator = " AND ").takeIf { it.isNotEmpty() }
}

private fun PageOptions.asLimitSql(): String {
    return "LIMIT $pageOffset OFFSET ${pageSize * pageOffset}"
}
