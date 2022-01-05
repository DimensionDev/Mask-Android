package com.dimension.maskbook.platform

import com.dimension.maskbook.wallet.platform.IPlatformSwitcher
import com.dimension.maskbook.wallet.repository.PlatformType

class PlatformSwitcher: IPlatformSwitcher {
    var actualSwitcher: IPlatformSwitcher? = null

    override fun switchTo(platformType: PlatformType) {
        actualSwitcher?.switchTo(platformType)
    }

    override fun showTooltips(visible: Boolean) {
        actualSwitcher?.showTooltips(visible)
    }

    override fun launchDeeplink(deeplink: String) {
        actualSwitcher?.launchDeeplink(deeplink)
    }

    override fun showModal(route: String, data: Any?) {
        actualSwitcher?.showModal(route, data)
    }
}