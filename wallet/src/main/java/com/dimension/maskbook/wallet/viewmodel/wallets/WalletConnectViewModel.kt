package com.dimension.maskbook.wallet.viewmodel.wallets

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.IWalletConnectRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WCWallet
import com.dimension.maskbook.wallet.walletconnect.WalletConnectClientManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WalletConnectViewModel(
    private val manager: WalletConnectClientManager,
    private val repository: IWalletConnectRepository,
    private val walletRepository: IWalletRepository,
    private val onResult: (success: Boolean, needToSwitchNetwork: Boolean) -> Unit,
) : ViewModel() {
    val network =
        walletRepository.dWebData.map { it.chainType }.asStateIn(viewModelScope, ChainType.eth)

    init {
        connect()
    }

    fun connect() {
        manager.connect(onResult = { success, responder ->
            viewModelScope.launch {
                var needToSwitchNetwork = false
                if (success) {
                    responder?.let {
                        // save it

                        val platform = walletRepository.dWebData.firstOrNull()?.coinPlatformType
                            ?: CoinPlatformType.Ethereum
                        repository.saveAccounts(responder = responder, platformType = platform)
                            ?.let {
                                walletRepository.setCurrentWallet(it)
                            }
                        needToSwitchNetwork =
                            walletRepository.currentWallet.firstOrNull()?.walletConnectChainType != network.value
                    }
                }
                onResult.invoke(success, needToSwitchNetwork)
            }
        })
    }

    val qrCode = manager.wcUrl.asStateIn(
        viewModelScope, ""
    )
    val wcUrl = manager.wcUrl.asStateIn(
        viewModelScope, ""
    )

    private val _chainType = MutableStateFlow(ChainType.eth)

    fun selectChain(chainType: ChainType) {
        _chainType.value = chainType
    }

    fun retry() {
        // reset session
        connect()
    }

    // not all wallet can handle: appScheme://wc?uri=wcUrl e.g. MetaMask, so we use origin wcUrl instead
    fun generateDeeplink(): Uri? = Uri.parse(wcUrl.value)

    private val _supportedWallets by lazy {
        repository.supportedWallets.asStateIn(
            viewModelScope, emptyList()
        )
    }

    val currentSupportedWallets = combine(_chainType, _supportedWallets) { type, wallets ->
        wallets.filter {
            it.isSupported(type)
        }
    }
}