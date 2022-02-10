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
package com.dimension.maskbook

import android.app.Application
import com.dimension.maskbook.common.CommonSetup
import com.dimension.maskbook.common.platform.IPlatformSwitcher
import com.dimension.maskbook.common.repository.JSMethod
import com.dimension.maskbook.handler.Web3MessageHandler
import com.dimension.maskbook.labs.LabsSetup
import com.dimension.maskbook.persona.PersonaSetup
import com.dimension.maskbook.platform.PlatformSwitcher
import com.dimension.maskbook.setting.SettingSetup
import com.dimension.maskbook.wallet.WalletSetup
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

class MaskbookApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@MaskbookApp)
            modules(
                CommonSetup.dependencyInject(),
                WalletSetup.dependencyInject(),
                SettingSetup.dependencyInject(),
                LabsSetup.dependencyInject(),
                PersonaSetup.dependencyInject(),
                platformModules,
            )
        }
    }
}

fun initModule() {
    CommonSetup.onExtensionReady()
    WalletSetup.onExtensionReady()
    SettingSetup.onExtensionReady()
    LabsSetup.onExtensionReady()
    PersonaSetup.onExtensionReady()
}

fun initEvent() {
    CoroutineScope(Dispatchers.IO).launch {
        launch {
            merge(
                JSMethod.Misc.openCreateWalletView(),
                JSMethod.Misc.openDashboardView(),
                JSMethod.Misc.openAppsView(),
                JSMethod.Misc.openSettingsView(),
            ).filter { uri ->
                uri.isNotEmpty()
            }.collect { uri ->
                KoinPlatformTools.defaultContext().get().get<IPlatformSwitcher>()
                    .launchDeeplink(uri)
            }
        }
        launch {
            JSMethod.Wallet.web3Event().mapNotNull { it }.collect {
                KoinPlatformTools.defaultContext().get().get<Web3MessageHandler>().handle(it)
            }
        }
        launch {
            JSMethod.Wallet.switchBlockChain().mapNotNull { it }.collect { data ->
                if (data.coinId != null) {
                    val platform =
                        CoinPlatformType.values().firstOrNull { it.coinId == data.coinId }
                    if (platform != null) {
                        KoinPlatformTools.defaultContext().get().get<IWalletRepository>()
                            .setActiveCoinPlatformType(platform)
                    }
                }
                if (data.networkId != null) {
                    val chainType = ChainType.values().firstOrNull { it.chainId == data.networkId }
                    if (chainType != null) {
                        KoinPlatformTools.defaultContext().get().get<IWalletRepository>()
                            .setChainType(chainType, false)
                    }
                }
            }
        }
    }
}

val platformModules = module {
    single { PlatformSwitcher() } binds arrayOf(
        IPlatformSwitcher::class,
        PlatformSwitcher::class,
    )
    single { Web3MessageHandler(get(), get()) }
}
