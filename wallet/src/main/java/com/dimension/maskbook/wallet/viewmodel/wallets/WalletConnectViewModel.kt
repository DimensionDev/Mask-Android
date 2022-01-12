package com.dimension.maskbook.wallet.viewmodel.wallets

import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.IWalletConnectRepository
import com.dimension.maskbook.wallet.repository.WCWallet
import com.dimension.maskbook.wallet.walletconnect.WalletConnectManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class WalletConnectViewModel(
    private val manager: WalletConnectManager,
    private val repository: IWalletConnectRepository,
    private val packageManager: PackageManager
) : ViewModel() {
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

    private val _supportedWallets by lazy {
        repository.supportedWallets.asStateIn(
            viewModelScope, emptyList()
        )
    }

    val currentSupportedWallets = combine(_chainType, _supportedWallets) { type, wallets ->
        wallets.filter {
            it.isSupported(type) { packageName ->
                // TODO check if package is installed
                packageName.isNotEmpty()
//                try {
//                    Log.d("Mimao", "${it.displayName} package:${it.packageName}")
//                    packageManager.getPackageInfo(packageName, 0)
//                    true
//                } catch (e: PackageManager.NameNotFoundException) {
//                    if (BuildConfig.DEBUG) e.printStackTrace()
//                    false
//                }
            }
        }
    }
}