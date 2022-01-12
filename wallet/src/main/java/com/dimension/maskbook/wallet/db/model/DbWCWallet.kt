package com.dimension.maskbook.wallet.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbWCWallet(
    @PrimaryKey val id: String,
    val name: String,
    val homePage: String,
    val nativeDeeplink: String,
    val universalLink: String,
    val shortName: String,
    val logo: String,
    val packageName: String,
    val chains: List<String>
)