/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.repository

import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.DbToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.math.BigDecimal

class TokenData(
    val address: String,
    val chainType: ChainType,
    val name: String,
    val symbol: String,
    val decimals: Long,
    val logoURI: String?,
    val price: BigDecimal,
) {
    companion object {
        fun fromDb(data: DbToken) = with(data) {
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
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TokenData

        if (address != other.address) return false
        if (chainType != other.chainType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + chainType.hashCode()
        return result
    }
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
