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

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull

class CollectiblesViewModel(
    private val repository: ICollectibleRepository,
    private val walletRepository: IWalletRepository,
) : ViewModel() {
    private val dataStore = mutableMapOf<String, Flow<PagingData<WalletCollectibleData>>>()

    fun getCollectibles(collectionSlug: String?) = dataStore.getOrPut(collectionSlug ?: "") {
        walletRepository.currentWallet.mapNotNull { it }.flatMapLatest {
            repository.getCollectiblesByWallet(walletData = it, collectionSlug = collectionSlug)
        }
    }
}
