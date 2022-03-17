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
package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.wallet.usecase.Result
import com.dimension.maskbook.wallet.usecase.collectible.GetWalletCollectibleCollectionsUseCase
import com.dimension.maskbook.wallet.usecase.token.GetWalletTokensUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SearchTradableViewModel(
    getWalletTokensUseCase: GetWalletTokensUseCase,
    geCollectionsUseCase: GetWalletCollectibleCollectionsUseCase,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateIn(viewModelScope)

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    val walletTokens by lazy {
        combine(query, getWalletTokensUseCase()) { q, result ->
            when (result) {
                is Result.Success -> result.value.filter {
                    if (q.isNotEmpty()) it.tokenData.name.contains(q, ignoreCase = true) else true
                }
                else -> emptyList()
            }
        }.asStateIn(viewModelScope, emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val walletCollectibleCollections by lazy {
        geCollectionsUseCase().mapNotNull {
            when (it) {
                is Result.Success -> it.value
                else -> null
            }
        }
    }
}
