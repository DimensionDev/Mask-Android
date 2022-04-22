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
package com.dimension.maskbook.wallet.repository.model

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.TokenData

data class DerivationPath(
    val purpose: Int,
    val coin: Int,
    val account: Int = 0,
    val change: Int = 0,
    val address: Int = 0,
) {
    override fun toString(): String {
        return "m/$purpose'/$coin'/$account'/$change/$address"
    }

    companion object {
        fun parse(value: String) = value
            .trimStart('m')
            .trimStart('/')
            .split("/")
            .map { it.trim('\'') }
            .map { it.toInt() }
            .let { DerivationPath(it[0], it[1], it[2], it[3], it[4]) }
    }
}

data class PendingTransaction(
    val transactionHash: String,
    val chainId: ChainType,
    val count: BigDecimal,
    val createdAt: Long,
    val token: TokenData,
)
