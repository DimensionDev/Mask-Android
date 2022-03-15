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
import com.dimension.maskbook.wallet.export.model.TradableData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.usecase.Result
import com.dimension.maskbook.wallet.usecase.address.GetAddressUseCase
import com.dimension.maskbook.wallet.usecase.chain.SetCurrentChainUseCase
import com.dimension.maskbook.wallet.usecase.collectible.GetWalletCollectibleUseCase
import com.dimension.maskbook.wallet.usecase.password.VerifyPaymentPasswordUseCase
import com.dimension.maskbook.wallet.usecase.token.GetWalletNativeTokenUseCase
import com.dimension.maskbook.wallet.usecase.token.GetWalletTokenByAddressUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.math.BigDecimal

class TransferDetailViewModel(
    private val tradableId: String,
    private val verifyPaymentPasswordUseCase: VerifyPaymentPasswordUseCase,
    private val getAddressUseCase: GetAddressUseCase,
    private val getWalletTokenByAddressUseCase: GetWalletTokenByAddressUseCase,
    private val getWalletNativeTokenUseCase: GetWalletNativeTokenUseCase,
    private val getWalletCollectibleUseCase: GetWalletCollectibleUseCase,
    private val setCurrentChainUseCase: SetCurrentChainUseCase
) : ViewModel() {

    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope)

    fun setPassword(value: String) {
        _password.value = value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val passwordValid by lazy {
        _password.flatMapLatest {
            verifyPaymentPasswordUseCase(it).map { result ->
                when (result) {
                    is Result.Success -> true
                    else -> false
                }
            }
        }.asStateIn(viewModelScope, false)
    }

    private val _amount = MutableStateFlow("0")
    val amount = _amount.asStateIn(viewModelScope)

    fun setAmount(value: String) {
        _amount.value = value
    }

    val balance by lazy {
        selectedTradable.map {
            when (it) {
                is WalletTokenData -> it.count
                else -> BigDecimal.ZERO
            }
        }.asStateIn(viewModelScope, BigDecimal.ZERO)
    }

    private val _gasTotal = MutableStateFlow(BigDecimal.ZERO)

    val maxAmount by lazy {
        combine(
            balance,
            selectedTradable,
            nativeToken,
            _gasTotal
        ) { balance, tradable, native, gasTotal ->
            when (tradable) {
                is WalletTokenData -> {
                    if (tradable.tradableId() == native?.tokenAddress) {
                        balance - gasTotal
                    } else {
                        balance
                    }.let {
                        if (it < BigDecimal.ZERO) BigDecimal.ZERO else it
                    }
                }
                else -> BigDecimal.ZERO
            }
        }.asStateIn(viewModelScope, BigDecimal.ZERO)
    }

    private val _toAddress = MutableStateFlow("")

    fun setAddress(address: String) {
        _toAddress.value = address
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val addressData by lazy {
        _toAddress.flatMapLatest { address ->
            getAddressUseCase(address)
                .map {
                    when (it) {
                        is Result.Success -> it.value
                        else -> null
                    }
                }
        }.asStateIn(viewModelScope, null)
    }

    private val nativeToken by lazy {
        getWalletNativeTokenUseCase().map { result ->
            when (result) {
                is Result.Success -> result.value
                else -> null
            }
        }
    }

    private val _walletTokenData = MutableStateFlow<WalletTokenData?>(null)
    private val walletTokenData by lazy {
        combine(
            _walletTokenData,
            getWalletTokenByAddressUseCase(tradableId),
            nativeToken
        ) { select, default, native ->
            select ?: when (default) {
                is Result.Success -> default.value
                else -> null
            } ?: native
        }
    }

    private val _collectibleData = MutableStateFlow<WalletCollectibleData?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val collectibleData by lazy {
        combine(_collectibleData, getWalletCollectibleUseCase(tradableId)) { select, default ->
            select ?: when (default) {
                is Result.Success -> default.value
                else -> null
            }
        }.asStateIn(viewModelScope, null)
    }

    val selectedTradable = combine(collectibleData, walletTokenData) { collectible, token ->
        collectible ?: token
    }.asStateIn(viewModelScope, null)

    fun onSelectTradable(value: TradableData) {
        when (value) {
            is WalletTokenData -> {
                _walletTokenData.value = value
                _collectibleData.value = null
            }
            is WalletCollectibleData -> {
                _collectibleData.value = value
                _walletTokenData.value = null
            }
        }
        setCurrentChainUseCase(value.network())
    }

    val canConfirm by lazy {
        combine(
            passwordValid,
            _amount.map { it.toBigDecimal() },
            selectedTradable.mapNotNull { it },
            maxAmount,
        ) { valid, amount, selectedData, maxAmount ->
            valid && when (selectedData) {
                is WalletCollectibleData -> true
                is WalletTokenData -> amount <= maxAmount
            }
        }.asStateIn(viewModelScope, false)
    }
}
