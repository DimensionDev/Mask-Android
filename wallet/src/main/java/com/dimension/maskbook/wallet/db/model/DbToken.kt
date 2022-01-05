package com.dimension.maskbook.wallet.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class DbToken(
    @PrimaryKey val id: String,
    val address: String,
    val chainId: String,
    val name: String,
    val symbol: String,
    val decimals: Long,
    val logoURI: String?,
    val price: BigDecimal,
)
