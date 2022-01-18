package com.dimension.maskbook

import android.app.Application
import android.content.Context
import com.dimension.maskbook.handler.Web3MessageHandler
import com.dimension.maskbook.platform.PlatformSwitcher
import com.dimension.maskbook.repository.*
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.platform.IPlatformSwitcher
import com.dimension.maskbook.wallet.repository.*
import com.dimension.maskbook.wallet.servicesModule
import com.dimension.maskbook.wallet.walletModules
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
            modules(repositoryModules, walletModules, platformModules, servicesModule)
        }
    }
}

fun initRepository() {
    KoinPlatformTools.defaultContext().get().get<IPersonaRepository>().init()
    KoinPlatformTools.defaultContext().get().get<IAppRepository>().init()
    KoinPlatformTools.defaultContext().get().get<IWalletRepository>().init()
    KoinPlatformTools.defaultContext().get().get<ISettingsRepository>().init()
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

val repositoryModules = module {
    single { PersonaRepository(get<Context>().personaDataStore, get(), get()) } binds arrayOf(
        IPersonaRepository::class,
        IContactsRepository::class
    )
    single { BackupRepository(get(), get<Context>().cacheDir, get<Context>().contentResolver) }
//    single<IPostRepository> { PostRepository() }
    single<IAppRepository> { AppRepository() }
//    single<IWalletRepository> { FakeWalletRepository() }
    single<ISettingsRepository> { SettingsRepository(get<Context>().settingsDataStore) }
    single<IWalletRepository> { WalletRepository(get<Context>().walletDataStore, get(), get()) }
    single<ICollectibleRepository> { CollectibleRepository(get(), get()) }
    single<ITransactionRepository> { TransactionRepository(get(), get()) }
    single<ITokenRepository> { TokenRepository(get()) }
    single<ISendHistoryRepository> { SendHistoryRepository(get()) }
    single<IWalletContactRepository> { WalletContactRepository(get()) }
}

val platformModules = module {
    single { PlatformSwitcher() } binds arrayOf(
        IPlatformSwitcher::class,
        PlatformSwitcher::class,
    )
    single { Web3MessageHandler(get(), get()) }
}