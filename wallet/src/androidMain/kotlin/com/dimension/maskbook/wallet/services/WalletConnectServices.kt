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
package com.dimension.maskbook.wallet.services

import android.content.Context
import com.dimension.maskbook.common.ext.JSON
import com.dimension.maskbook.wallet.services.model.WCSupportedWallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString

interface WalletConnectServices {
    suspend fun supportedWallets(): Map<String, WCSupportedWallet>
}

/**
 * many wallet's info from https://registry.walletconnect.com/api/v1/wallets missing supported chains
 * Use local json file instead just like the Mask iOS app
 */
class LocalJsonWalletConnectServices(private val context: Context) : WalletConnectServices {
    override suspend fun supportedWallets(): Map<String, WCSupportedWallet> {
        return withContext(Dispatchers.IO) {
            context.assets.open("wallet_connect.json").use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                JSON.decodeFromString(json)
            }
        }
    }
}
