package com.dimension.maskbook.wallet.walletconnect

import com.dimension.maskbook.wallet.repository.ChainType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal


// wallets that supported wallet connection
// https://registry.walletconnect.org/data/wallets.json


interface WalletConnectClientManager {
    val wcUrl: Flow<String>
    fun connect(onResult: (success: Boolean, responder: WCResponder?) -> Unit)
    fun disConnect(address: String): Boolean
    fun initSessions(onDisconnect: (address: String) -> Unit)
    fun sendToken(
        amount: BigDecimal, // ether
        fromAddress: String,
        toAddress: String,
        data: String,
        gasLimit: Double,
        gasPrice: BigDecimal,// ether
    )
}

data class WCResponder(
    val accounts: List<String>,
    val name: String,
    val description: String,
    val icons: List<String>,
    val url: String,
    val chainType: ChainType,
)