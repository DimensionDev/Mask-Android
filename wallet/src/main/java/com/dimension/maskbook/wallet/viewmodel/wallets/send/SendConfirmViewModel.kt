/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.TokenData
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.math.BigDecimal

class SendConfirmViewModel(
    private val toAddress: String,
    private val sendHistoryRepository: ISendHistoryRepository,
    private val walletRepository: IWalletRepository,
) : ViewModel() {

    fun send(
        tokenData: TokenData,
        amount: BigDecimal,
        gasLimit: Double,
        gasFee: BigDecimal,
        maxFee: Double,
        maxPriorityFee: Double
    ) {
        walletRepository.sendTokenWithCurrentWallet(
            amount = amount,
            address = toAddress,
            tokenData = tokenData,
            gasLimit = gasLimit,
            gasFee = gasFee,
            maxFee = maxFee,
            maxPriorityFee = maxPriorityFee,
        )
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
