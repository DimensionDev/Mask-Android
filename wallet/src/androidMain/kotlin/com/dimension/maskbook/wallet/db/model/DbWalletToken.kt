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
package com.dimension.maskbook.wallet.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.dimension.maskbook.common.bigDecimal.BigDecimal

@Entity(
    indices = [Index(value = ["walletId", "tokenId"], unique = true)],
)
data class DbWalletToken(
    @PrimaryKey val id: String,
    val walletId: String,
    val count: BigDecimal,
    val tokenId: String,
)

data class DbWalletTokenWithToken(
    @Embedded
    val reference: DbWalletToken,
    @Relation(
        parentColumn = "tokenId",
        entityColumn = "id",
    )
    val token: DbToken
)

data class DbWalletTokenTokenWithWallet(
    @Embedded
    val wallet: DbWallet,
    @Relation(
        parentColumn = "id",
        entityColumn = "walletId",
        entity = DbWalletToken::class
    )
    val items: List<DbWalletTokenWithToken>,
    @Relation(
        parentColumn = "storeKeyId",
        entityColumn = "id",
    )
    val storedKey: DbStoredKey,
    @Relation(
        parentColumn = "id",
        entityColumn = "walletId",
    )
    val balance: List<DbWalletBalance>
)
