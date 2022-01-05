package com.dimension.maskbook.wallet.platform

import com.dimension.maskbook.wallet.repository.PlatformType

interface IPlatformSwitcher {
    fun switchTo(platformType: PlatformType)
    fun showTooltips(visible: Boolean)
    fun launchDeeplink(deeplink: String)
    fun showModal(route: String, data: Any?)
}