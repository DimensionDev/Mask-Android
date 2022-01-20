package com.dimension.maskbook.wallet.services

import com.dimension.maskbook.wallet.services.model.WCSupportedWallet
import retrofit2.http.GET


interface WalletConnectServices {
    @GET("/data/wallets.json")
    suspend fun supportedWallets():Map<String, WCSupportedWallet>
}
