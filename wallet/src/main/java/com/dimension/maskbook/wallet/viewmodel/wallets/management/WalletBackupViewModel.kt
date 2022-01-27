package com.dimension.maskbook.wallet.viewmodel.wallets.management

import androidx.lifecycle.ViewModel
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull

class WalletBackupViewModel(
    private val repository: IWalletRepository,
    private val settingsRepository: ISettingsRepository
) : ViewModel() {
    val keyStore by lazy {
        combine(
            repository.currentWallet.mapNotNull { it },
            repository.dWebData,
            settingsRepository.paymentPassword,
        ) { wallet, data, password ->
            repository.getKeyStore(walletData = wallet, platformType = data.coinPlatformType, password)
        }
    }
    val privateKey by lazy {
        combine(
            repository.currentWallet.mapNotNull { it },
            repository.dWebData,
        ) { wallet, data ->
            repository.getPrivateKey(walletData = wallet, platformType = data.coinPlatformType)
        }
    }
}