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
package com.dimension.maskbook.wallet.ui.tab

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.WalletIntroHost
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

class WalletTabScreen : TabScreen {
    override val route = CommonRoute.Main.Tabs.Wallet
    override val title: Int = R.string.tab_wallet
    override val icon: Int = R.drawable.ic_wallet

    @OptIn(ExperimentalMaterialNavigationApi::class)
    @Composable
    override fun Content(navController: NavController) {
        WalletIntroHost(navController)
    }
}
