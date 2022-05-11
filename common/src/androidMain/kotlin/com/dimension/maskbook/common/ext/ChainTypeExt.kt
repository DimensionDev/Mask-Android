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
package com.dimension.maskbook.common.ext

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.dimension.maskbook.common.R
import com.dimension.maskbook.common.okhttp.okHttpClient
import com.dimension.maskbook.wallet.export.model.ChainType
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

val ChainType.httpService: HttpService
    get() = HttpService(endpoint, okHttpClient)

val ChainType.web3j: Web3j
    get() = Web3j.build(httpService)

val ChainType.onDrawableRes: Int
    get() = when (this) {
        ChainType.eth -> R.drawable.ethereum_o1_2
        ChainType.bsc -> R.drawable.binance_2
        ChainType.polygon -> R.drawable.polygon_2
        ChainType.arbitrum -> R.drawable.logos_and_symbols
        ChainType.xdai -> R.drawable.ic_xdai_on
        else -> -1
    }

val ChainType.backgroundColor: Color
    get() = when (this) {
        ChainType.eth -> Color(0XFF3557D9)
        ChainType.bsc -> Color(0XFFF9A708)
        ChainType.polygon -> Color(0XFF4D0FC1)
        ChainType.arbitrum -> Color(0XFF1C2B6B)
        ChainType.xdai -> Color(0XFF219999)
        else -> Color(0XFF212E59)
    }

val ChainType.backgroundBrush: Brush
    get() = when (this) {
        ChainType.eth -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF2346CA),
                Color(0xFF627EEA),
            )
        )
        ChainType.bsc -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFEFAC00),
                Color(0xFFFFCC4E),
            )
        )
        ChainType.polygon -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF6428D4),
                Color(0xFF8547F9),
            )
        )
        ChainType.xdai -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF1A9494),
                Color(0xFF56C6C6),
            )
        )
        ChainType.arbitrum -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF051942),
                Color(0xFF2A9DEB),
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF0049CE),
                Color(0xFF1C68F3),
            )
        )
    }

val ChainType.shortName: String
    get() = when (this) {
        ChainType.xdai -> "Gnosis"
        ChainType.eth -> "Ethereum"
        ChainType.rinkeby -> "Rinkeby"
        ChainType.bsc -> "Bnb"
        ChainType.polygon -> "Polygon"
        ChainType.arbitrum -> "Arbitrum"
        ChainType.optimism -> "Optimism"
        ChainType.polka -> "Polka"
        ChainType.kovan -> "Kovan"
        ChainType.goerli -> "Goerli"
        ChainType.kusama -> "Kusama"
        ChainType.westend -> "Westend"
        ChainType.edgeware -> "Edgeware"
        ChainType.polkadot -> "Polkadot"
        ChainType.node -> "Node"
        ChainType.custom -> "Custom"
        ChainType.unknown -> "Unknown"
    }

val ChainType.offDrawableRes: Int
    get() = when (this) {
        ChainType.eth -> R.drawable.ethereum_o1_1
        ChainType.bsc -> R.drawable.binance_1
        ChainType.polygon -> R.drawable.polygon1
        ChainType.arbitrum -> R.drawable.logos_and_symbols_1
        ChainType.xdai -> R.drawable.ic_xdai_off
        else -> -1
    }

val ChainType.primaryColor: Color
    get() = when (this) {
        ChainType.eth -> Color(0xFF627EEA)
        ChainType.bsc -> Color(0xFFF3BA2F)
        ChainType.polygon -> Color(0xFF8247E5)
        ChainType.arbitrum -> Color(0xFF28A0F0)
        ChainType.xdai -> Color(0xFF48A9A6)
        else -> Color.Transparent
    }
