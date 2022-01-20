package com.dimension.maskbook.wallet.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.dimension.maskbook.wallet.db.dao.*
import com.dimension.maskbook.wallet.db.model.*
import com.dimension.maskbook.wallet.ext.JSON
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.math.BigDecimal

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
    ],
    version = 6,
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
