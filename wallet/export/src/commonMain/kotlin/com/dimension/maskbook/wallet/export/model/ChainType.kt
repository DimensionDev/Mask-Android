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
package com.dimension.maskbook.wallet.repository

import kotlinx.serialization.Serializable

@Serializable
enum class ChainType(
    val chainId: Long,
    val endpoint: String,
    val supportEip25519: Boolean,
) {
    eth(1, "https://mainnet.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", true),
    rinkeby(4, "https://rinkeby.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", true),
    bsc(56, "https://bsc-dataseed.binance.org", false),
    polygon(137, "https://polygon-mainnet.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", false),
    arbitrum(42_161, "https://arb1.arbitrum.io/rpc", false),
    xdai(100, "https://rpc.xdaichain.com", false),
    optimism(10, "https://mainnet.optimism.io", false),
    polka(1, "", false), // TODO: json rpc endpoint
    kovan(42, "https://kovan.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", true),
    goerli(5, "https://goerli.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", true),
    kusama(8, "https://kusama-rpc.polkadot.io/", false),
    westend(9, "https://westend-rpc.polkadot.io/", false),
    edgeware(10, "https://edgeware-node.edgewa.re/", false),
    polkadot(17, "https://polkadot-rpc.polkadot.io/", false),
    node(0, "", false),
    custom(0, "", false),
    unknown(0, "", false),
}
