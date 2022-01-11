package com.dimension.maskbook.wallet.viewmodel.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IAppRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository

class LabsViewModel(
    private val repository: IAppRepository,
    private val walletRepository: IWalletRepository,
) : ViewModel() {

    init {
        repository.init()
    }

    val apps by lazy {
        repository.apps.asStateIn(viewModelScope, emptyList())
    }

    val wallet by lazy {
        walletRepository.currentWallet.asStateIn(viewModelScope, null)
    }

    fun setEnabled(id: String, enabled: Boolean) {
        repository.setEnabled(id, enabled)
    }
}