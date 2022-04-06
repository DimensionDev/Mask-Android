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
package com.dimension.maskbook.wallet.ext

import com.dimension.maskbook.debankapi.model.ChainID
import com.dimension.maskbook.wallet.export.model.ChainType

val ChainType.dbank: ChainID
    get() = when (this) {
        ChainType.eth -> ChainID.eth
        ChainType.rinkeby -> ChainID.eth
        ChainType.bsc -> ChainID.bsc
        ChainType.polygon -> ChainID.matic
        ChainType.arbitrum -> ChainID.arb
        ChainType.xdai -> ChainID.xdai
        ChainType.optimism -> ChainID.op
        ChainType.polka -> ChainID.eth
        else -> throw NotImplementedError("ChainType $this not supported")
    }

val ChainID.chainType: ChainType
    get() = when (this) {
        ChainID.eth -> ChainType.eth
        ChainID.bsc -> ChainType.bsc
        ChainID.matic -> ChainType.polygon
        ChainID.arb -> ChainType.arbitrum
        ChainID.xdai -> ChainType.xdai
        ChainID.op -> ChainType.optimism
        else -> ChainType.unknown
    }
