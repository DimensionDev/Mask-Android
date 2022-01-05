package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.TokenData
import kotlinx.coroutines.flow.mapNotNull
import java.math.BigDecimal

class SendConfirmViewModel(
    private val tokenData: TokenData,
    private val toAddress: String,
    private val sendHistoryRepository: ISendHistoryRepository,
    private val walletRepository: IWalletRepository,
): ViewModel() {

    fun send(
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
}