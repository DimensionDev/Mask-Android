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
package com.dimension.maskbook.wallet.repository

import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.DbToken
import com.dimension.maskbook.wallet.export.model.TokenData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

fun TokenData.Companion.fromDb(data: DbToken) = with(data) {
    TokenData(
        address,
        chainType,
        name,
        symbol,
        decimals,
        logoURI,
        price,
    )
}

interface ITokenRepository {
    fun getTokenByAddress(id: String): Flow<TokenData>
}

class TokenRepository(
    private val database: AppDatabase,
) : ITokenRepository {
    override fun getTokenByAddress(id: String): Flow<TokenData> {
        return database.tokenDao().getByIdFlow(id).mapNotNull {
            it?.let { token -> TokenData.fromDb(token) }
        }
    }
}
