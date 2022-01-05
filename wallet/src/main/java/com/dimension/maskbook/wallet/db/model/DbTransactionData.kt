package com.dimension.maskbook.wallet.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

enum class DbTransactionType {
    Swap,
    Receive,
    Send,
}

enum class DbTransactionStatus {
    Success,
    Failure,
    Pending,
}

@Entity
data class DbTransactionData(
    @PrimaryKey val id: String,
    val transactionId: String,
    val type: DbTransactionType,
    val count: BigDecimal,
    val tokenAddress: String,
    val status: DbTransactionStatus,
    val message: String,
    val createdAt: Long,
    val updatedAt: Long,
)