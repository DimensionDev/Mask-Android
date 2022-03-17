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
package com.dimension.maskbook.wallet

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.room.Room
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.wallet.data.JSMethod
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.RoomMigrations
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.handler.Web3MessageHandler
import com.dimension.maskbook.wallet.repository.IWalletConnectRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.route.generatedRoute
import com.dimension.maskbook.wallet.walletconnect.WalletConnectClientManager
import com.dimension.maskbook.wallet.walletconnect.WalletConnectServerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import org.koin.core.annotation.ComponentScan
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

@org.koin.core.annotation.Module
@ComponentScan
object WalletSetup : ModuleSetup {

    override fun NavGraphBuilder.route(navController: NavController) {
        generatedRoute(navController)
    }

    override fun dependencyInject(): Module = module {
        single {
            Room.databaseBuilder(get(), AppDatabase::class.java, "maskbook")
                .setQueryExecutor(Dispatchers.IO.asExecutor())
                .setTransactionExecutor(Dispatchers.IO.asExecutor())
                .addMigrations(
                    RoomMigrations.MIGRATION_6_7,
                    RoomMigrations.MIGRATION_7_8,
                    RoomMigrations.MIGRATION_8_9
                )
                .build()
        }
    }

    override fun onExtensionReady() {
        initRepository()
        initWalletConnect()
        initEvent()
    }
}

private fun initEvent() {
    with(KoinPlatformTools.defaultContext().get()) {
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                get<JSMethod>().web3Event().collect {
                    get<Web3MessageHandler>().handle(it)
                }
            }
            launch {
                get<JSMethod>().switchBlockChain().collect { data ->
                    if (data.coinId != null) {
                        val platform = CoinPlatformType.values().firstOrNull { it.coinId == data.coinId }
                        if (platform != null) {
                            get<IWalletRepository>().setActiveCoinPlatformType(platform)
                        }
                    }
                    if (data.networkId != null) {
                        val chainType = ChainType.values().firstOrNull { it.chainId == data.networkId }
                        if (chainType != null) {
                            get<IWalletRepository>().setChainType(chainType, false)
                        }
                    }
                }
            }
        }
    }
}

private fun initRepository() {
    KoinPlatformTools.defaultContext().get().get<IWalletRepository>().init()
    KoinPlatformTools.defaultContext().get().get<IWalletConnectRepository>().init()
}

private fun initWalletConnect() {
    val walletRepository = KoinPlatformTools.defaultContext().get().get<IWalletRepository>()
    KoinPlatformTools.defaultContext().get().get<WalletConnectClientManager>()
        .initSessions { address ->
            CoroutineScope(Dispatchers.IO).launch {
                walletRepository.findWalletByAddress(address)?.let { wallet ->
                    walletRepository.deleteWallet(wallet.id)
                }
            }
        }
    KoinPlatformTools.defaultContext().get().get<WalletConnectServerManager>()
        .init { _, _ -> // clientMeta, request ->
            TODO("navigate to wallet connect request handle scene")
        }
}
