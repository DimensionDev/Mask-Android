package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.IWalletContactRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.math.BigDecimal

class SearchAddressViewModel(
    private val walletRepository: IWalletRepository,
    private val sendHistoryRepository: ISendHistoryRepository,
    private val walletContactRepository: IWalletContactRepository,
) : ViewModel() {
    val contacts by lazy {
        combine(walletContactRepository.contacts, input) { contacts, input ->
            if (input.isEmpty()) {
                contacts
            } else {
                contacts.filter {
                    it.name?.contains(input, ignoreCase = true) == true ||
                            it.address.contains(input, ignoreCase = true)
                }
            }
        }
    }
    val recent by lazy {
        combine(sendHistoryRepository.recent, input) { recent, input ->
            if (input.isEmpty()) {
                recent
            } else {
                recent.filter {
                    it.name?.contains(input, ignoreCase = true) == true ||
                            it.address.contains(input, ignoreCase = true)
                }
            }
        }
    }
    val noTokenFound by lazy {
        walletRepository.currentWallet.mapNotNull { it }
            .map { it.tokens.firstOrNull { it.tokenData.address == "eth" } }
            .mapNotNull { it }
            .map { it.count == BigDecimal.ZERO }
    }
    private val _input = MutableStateFlow("")
    val input = _input.asStateIn(viewModelScope, "")
//    private val _loading = MutableStateFlow(false)
//    val loading = _loading.asStateIn(viewModelScope, false)
//    private val _searchResult = MutableStateFlow<SearchAddressResult?>(null)
//    val searchResult by lazy {
//        combine(input, _searchResult) { currentInput, result ->
//            if (currentInput != result?.query) {
//                null
//            } else {
//                result
//            }
//        }.asStateIn(viewModelScope, null)
//    }

    fun onInputChanged(value: String) {
        _input.value = value
    }

    fun addSendHistory(it: String) {
        sendHistoryRepository.addOrUpdate(it)
    }
}