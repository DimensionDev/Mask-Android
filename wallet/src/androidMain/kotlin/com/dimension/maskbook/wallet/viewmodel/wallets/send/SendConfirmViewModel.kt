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
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.wallet.export.model.TradableData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class SendConfirmViewModel(
    private val toAddress: String,
    private val sendHistoryRepository: ISendHistoryRepository,
    private val walletRepository: IWalletRepository,
) : ViewModel() {

    fun send(
        tradableData: TradableData,
        amount: BigDecimal,
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double
    ) {
        when (tradableData) {
            is WalletTokenData -> walletRepository.sendTokenWithCurrentWallet(
                amount = amount,
                address = toAddress,
                tokenData = tradableData.tokenData,
                gasLimit = gasLimit,
                maxFee = maxFee,
                maxPriorityFee = maxPriorityFee,
            )
            is WalletCollectibleData -> walletRepository.sendCollectibleWithCurrentWallet(
                address = toAddress,
                collectible = tradableData,
                gasLimit = gasLimit,
                maxFee = maxFee,
                maxPriorityFee = maxPriorityFee,
            )
        }
    }

    val addressData by lazy {
        sendHistoryRepository.getByAddress(toAddress)
            .asStateIn(viewModelScope, null)
            .mapNotNull { it }
    }

    val deepLink = walletRepository.currentWallet.map {
        it?.walletConnectDeepLink ?: ""
    }.asStateIn(viewModelScope, "")
}
