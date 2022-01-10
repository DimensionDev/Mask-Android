package com.dimension.maskbook.wallet.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.dimension.maskbook.wallet.db.dao.*
import com.dimension.maskbook.wallet.db.model.*
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
    ],
    version = 3,
)
@TypeConverters(BigDecimalTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun transactionDao(): TransactionDao
    abstract fun walletDao(): WalletDao
    abstract fun walletTokenDao(): WalletTokenDao
    abstract fun sendHistoryDao(): SendHistoryDao
    abstract fun walletContactDao(): WalletContactDao
    abstract fun storedKeyDao(): StoredKeyDao
    abstract fun walletBalanceDao(): WalletBalanceDao
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
