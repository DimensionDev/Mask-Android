package com.dimension.maskbook.wallet.viewmodel.wallets.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ITokenRepository
import com.dimension.maskbook.wallet.repository.ITransactionRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull

class WalletTransactionHistoryViewModel(
    private val repository: IWalletRepository,
    private val transactionRepository: ITransactionRepository,
): ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions by lazy {
        repository.currentWallet.mapNotNull { it }.mapLatest {
            transactionRepository.getTransactionByWallet(it)
        }
    }
}