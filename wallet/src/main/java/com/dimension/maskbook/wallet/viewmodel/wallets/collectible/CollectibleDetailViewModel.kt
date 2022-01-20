package com.dimension.maskbook.wallet.viewmodel.wallets.collectible

import androidx.lifecycle.ViewModel
import com.dimension.maskbook.wallet.repository.ICollectibleRepository

class CollectibleDetailViewModel(
    private val id: String,
    private val repository: ICollectibleRepository,
): ViewModel() {
    val data by lazy {
        repository.getCollectibleById(id)
    }
}