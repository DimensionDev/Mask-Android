package com.dimension.maskbook.wallet.walletconnect

import kotlinx.coroutines.flow.Flow


// wallets that supported wallet connection
// https://registry.walletconnect.org/data/wallets.json


interface WalletConnectClientManager {
    val wcUrl: Flow<String>
    fun connect(onResult: (success: Boolean, responder:WCResponder?) -> Unit)
    fun disConnect(address: String): Boolean
    fun initSessions(onDisconnect:(address: String) -> Unit)
}

data class WCResponder(
    val accounts: List<String>,
    val name : String,
    val description: String,
    val icons: List<String>,
    val url: String,
)