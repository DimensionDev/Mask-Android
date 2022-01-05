package com.dimension.maskbook.wallet.db.model

import androidx.room.*
import java.math.BigDecimal

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