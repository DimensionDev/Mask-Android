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
package com.dimension.maskbook.wallet.viewmodel.wallets.collectible

import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.util.DateUtils
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.ITransactionRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class CollectibleDetailViewModel(
    private val id: String,
    private val repository: ICollectibleRepository,
    private val walletRepository: IWalletRepository,
    private val transactionRepository: ITransactionRepository
) : ViewModel() {
    val data by lazy {
        repository.getCollectibleById(id)
    }

    val transactions by lazy {
        combine(
            walletRepository.currentWallet.filterNotNull(),
            data.filterNotNull()
        ) { wallet, collectible ->
            transactionRepository.getTransactionByCollectible(wallet, collectible)
                .asSequence()
                .sortedByDescending { it.createdAt }
                .groupBy { DateUtils.getDateText(it.createdAt) }
                .toMap()
        }.asStateIn(viewModelScope, emptyMap())
    }
}
