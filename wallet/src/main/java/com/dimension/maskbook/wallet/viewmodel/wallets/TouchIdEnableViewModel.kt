package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel

class TouchIdEnableViewModel : ViewModel() {
    fun enable(onEnable: () -> Unit) {
        /*TODO Logic: enable touch id unlock*/
        onEnable.invoke()
    }
}