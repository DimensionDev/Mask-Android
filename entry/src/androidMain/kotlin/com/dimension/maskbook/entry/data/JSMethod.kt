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
package com.dimension.maskbook.entry.data

import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.extension.export.ExtensionServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
internal class JSMethod(
    private val extensionServices: ExtensionServices,
) {
    fun openCreateWalletView(): Flow<String> {
        return extensionServices.subscribeJSEvent("misc_openCreateWalletView").map {
            if (!it.params.isNullOrEmpty()) Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona) else ""
        }
    }

    fun openDashboardView(): Flow<String> {
        return extensionServices.subscribeJSEvent("misc_openDashboardView").map {
            if (!it.params.isNullOrEmpty()) Deeplinks.Main.Home(CommonRoute.Main.Tabs.Wallet) else ""
        }
    }

    fun openAppsView(): Flow<String> {
        return extensionServices.subscribeJSEvent("misc_openAppsView").map {
            if (!it.params.isNullOrEmpty()) Deeplinks.Main.Home(CommonRoute.Main.Tabs.Labs) else ""
        }
    }

    fun openSettingsView(): Flow<String> {
        return extensionServices.subscribeJSEvent("misc_openSettingsView").map {
            if (!it.params.isNullOrEmpty()) Deeplinks.Main.Home(CommonRoute.Main.Tabs.Setting) else ""
        }
    }
}
