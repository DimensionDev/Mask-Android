package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel

class FaceIdEnableViewModel : ViewModel() {
    fun enable(onEnable: () -> Unit) {
        /*TODO Logic: enable face id unlock*/
        onEnable.invoke()
    }
}