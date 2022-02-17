/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
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
