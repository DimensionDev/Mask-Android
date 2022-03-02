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
package com.dimension.maskbook.wallet.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.JSON
import com.dimension.maskbook.wallet.db.dao.ChainDao
import com.dimension.maskbook.wallet.db.dao.CollectibleDao
import com.dimension.maskbook.wallet.db.dao.SendHistoryDao
import com.dimension.maskbook.wallet.db.dao.StoredKeyDao
import com.dimension.maskbook.wallet.db.dao.TokenDao
import com.dimension.maskbook.wallet.db.dao.TransactionDao
import com.dimension.maskbook.wallet.db.dao.WCWalletDao
import com.dimension.maskbook.wallet.db.dao.WalletBalanceDao
import com.dimension.maskbook.wallet.db.dao.WalletContactDao
import com.dimension.maskbook.wallet.db.dao.WalletDao
import com.dimension.maskbook.wallet.db.dao.WalletTokenDao
import com.dimension.maskbook.wallet.db.model.DbChainData
import com.dimension.maskbook.wallet.db.model.DbCollectible
import com.dimension.maskbook.wallet.db.model.DbSendHistory
import com.dimension.maskbook.wallet.db.model.DbStoredKey
import com.dimension.maskbook.wallet.db.model.DbToken
import com.dimension.maskbook.wallet.db.model.DbTransactionData
import com.dimension.maskbook.wallet.db.model.DbWCWallet
import com.dimension.maskbook.wallet.db.model.DbWallet
import com.dimension.maskbook.wallet.db.model.DbWalletBalance
import com.dimension.maskbook.wallet.db.model.DbWalletContact
import com.dimension.maskbook.wallet.db.model.DbWalletToken
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

@Database(
    entities = [
        DbToken::class,
        DbWallet::class,
        DbTransactionData::class,
        DbWalletToken::class,
        DbSendHistory::class,
        DbWalletContact::class,
        DbStoredKey::class,
        DbWalletBalance::class,
        DbCollectible::class,
        DbWCWallet::class,
        DbChainData::class,
    ],
    version = 9,
)
@TypeConverters(BigDecimalTypeConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun transactionDao(): TransactionDao
    abstract fun walletDao(): WalletDao
    abstract fun walletTokenDao(): WalletTokenDao
    abstract fun sendHistoryDao(): SendHistoryDao
    abstract fun walletContactDao(): WalletContactDao
    abstract fun storedKeyDao(): StoredKeyDao
    abstract fun walletBalanceDao(): WalletBalanceDao
    abstract fun collectibleDao(): CollectibleDao
    abstract fun wcWalletDao(): WCWalletDao
    abstract fun chainDao(): ChainDao
}

class BigDecimalTypeConverter {
    @TypeConverter
    fun bigDecimalToString(input: BigDecimal?): String {
        return input?.toPlainString() ?: ""
    }

    @TypeConverter
    fun stringToBigDecimal(input: String?): BigDecimal {
        if (input.isNullOrBlank()) return BigDecimal.valueOf(0.0)
        return input.toBigDecimalOrNull() ?: BigDecimal.valueOf(0.0)
    }
}

internal class StringListConverter {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.let {
            JSON.decodeFromString<List<String>>(it)
        } ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list?.let {
            JSON.encodeToString(it)
        } ?: "[]"
    }
}
