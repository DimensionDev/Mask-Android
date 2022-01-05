package com.dimension.maskbook.wallet.viewmodel.wallets.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.MutableStateFlow

class WalletRenameViewModel(
    private val id: String,
    private val repository: IWalletRepository,
): ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateIn(viewModelScope, "")
    fun setName(value: String) {
        _name.value = value
    }
    fun confirm() {
        repository.renameWallet(_name.value, id)
    }
}