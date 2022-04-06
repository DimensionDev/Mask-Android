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