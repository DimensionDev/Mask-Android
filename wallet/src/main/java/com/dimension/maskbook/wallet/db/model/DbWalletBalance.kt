package com.dimension.maskbook.wallet.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    indices = [Index(value = ["walletId", "type"], unique = true)],
)
data class DbWalletBalance(
    @PrimaryKey val id: String,
    val walletId: String,
    val type: DbWalletBalanceType,
    val value: BigDecimal,
)

enum class DbWalletBalanceType {
    all,
    eth,
    rinkeby,
    bsc,
    polygon,
    arbitrum,
    xdai,
    optimism,
    polka,
}