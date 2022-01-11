package com.dimension.maskbook.wallet.walletconnect

import android.content.Context
import android.content.pm.PackageManager
import com.dimension.maskbook.wallet.repository.ChainType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable


// wallets that supported wallet connection
// https://registry.walletconnect.org/data/wallets.json


interface WalletConnectManager {
    fun setUp()
    fun shutDown()
    val wcUrl: Flow<String>
}

interface WalletConnectionCallback{

}