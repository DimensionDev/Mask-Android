package com.dimension.maskbook.wallet.usecase

import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.repository.IWalletRepository

class SetCurrentChainUseCase(
    private val repository: IWalletRepository
) {
    operator fun invoke(chainType: ChainType) = runCatching { repository.setChainType(networkType = chainType) }
}