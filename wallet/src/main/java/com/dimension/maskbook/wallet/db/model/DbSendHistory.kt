package com.dimension.maskbook.wallet.db.model

import androidx.room.*

@Entity(
    indices = [Index(value = ["address"], unique = true)],
)
data class DbSendHistory(
    @PrimaryKey val id: String,
    val address: String,
    val lastSend: Long,
    val contactId: String?,
)

data class DbSendHistoryWithContact(
    @Embedded
    val history: DbSendHistory,
    @Relation(
        parentColumn = "contactId",
        entityColumn = "id",
    )
    val contact: DbWalletContact?,
)