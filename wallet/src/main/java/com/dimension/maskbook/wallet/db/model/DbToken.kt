package com.dimension.maskbook.wallet.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dimension.maskbook.wallet.repository.ChainType
import java.math.BigDecimal

@Entity
data class DbToken(
    @PrimaryKey val id: String,
    val address: String,
    val chainType: ChainType,
    val name: String,
    val symbol: String,
    val decimals: Long,
    val logoURI: String?,
    val price: BigDecimal,
)
