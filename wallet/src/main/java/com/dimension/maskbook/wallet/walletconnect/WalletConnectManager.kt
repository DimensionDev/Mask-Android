package com.dimension.maskbook.wallet.walletconnect

import kotlinx.coroutines.flow.Flow


// wallets that supported wallet connection
// https://registry.walletconnect.org/data/wallets.json

interface WalletConnectManager {
    fun setUp()
    fun shutDown()
    val wcUrl: Flow<String>
}

interface WalletConnectionCallback{

}