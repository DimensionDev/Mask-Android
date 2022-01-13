package com.dimension.maskbook.wallet.walletconnect

import kotlinx.coroutines.flow.Flow


// wallets that supported wallet connection
// https://registry.walletconnect.org/data/wallets.json


interface WalletConnectClientManager {
    val wcUrl: Flow<String>
    fun connect(onResult: (success: Boolean, wcUrl: String, approvedAccounts:List<String>) -> Unit)
    fun disConnect(wcUrl: String)
}