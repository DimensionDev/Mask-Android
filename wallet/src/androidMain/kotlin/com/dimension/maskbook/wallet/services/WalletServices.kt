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
import com.dimension.maskbook.common.retrofit.retrofit
import com.dimension.maskbook.debankapi.api.DebankResources
import org.koin.core.annotation.Single

@Single
class WalletServices(private val context: Context) {
    val debankServices by lazy {
        retrofit<DebankResources>("https://openapi.debank.com")
    }
    val gasServices by lazy {
        retrofit<GasServices>("https://ethgasstation.info")
    }
    val openSeaServices by lazy {
        retrofit<OpenSeaServices>("https://api.opensea.io")
    }
    val etherscanServices by lazy {
        retrofit<EtherscanServices>("https://api.etherscan.io")
    }

    val walletConnectServices by lazy {
        LocalJsonWalletConnectServices(context)
    }
}
