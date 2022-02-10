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
package com.dimension.maskbook.platform

import com.dimension.maskbook.common.platform.IPlatformSwitcher
import com.dimension.maskbook.wallet.repository.PlatformType

class PlatformSwitcher : IPlatformSwitcher {
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
