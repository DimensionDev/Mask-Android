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
package com.dimension.maskbook.wallet

import com.dimension.maskbook.wallet.export.WalletServices
import com.dimension.maskbook.wallet.export.model.ChainData
import com.dimension.maskbook.wallet.export.model.WalletData
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.util.SignUtils
import kotlinx.coroutines.flow.Flow

class WalletServicesImpl(
    private val walletRepository: IWalletRepository,
) : WalletServices {
    override val currentWallet: Flow<WalletData?>
        get() = walletRepository.currentWallet

    override val currentChain: Flow<ChainData?>
        get() = walletRepository.currentChain

    override fun generateNewMnemonic(): List<String> {
        return walletRepository.generateNewMnemonic()
    }

    override fun validateMnemonic(mnemonic: String): Boolean {
        return walletRepository.validateMnemonic(mnemonic)
    }

    override fun validatePrivateKey(privateKey: String): Boolean {
        return walletRepository.validatePrivateKey(privateKey)
    }

    override fun signMessage(message: String, password: String): String {
        return SignUtils.signMessage(message, password)
    }
}
