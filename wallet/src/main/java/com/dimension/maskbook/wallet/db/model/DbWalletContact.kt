package com.dimension.maskbook.wallet.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["address"], unique = true)],
)
data class DbWalletContact(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
)