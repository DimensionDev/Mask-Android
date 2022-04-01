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

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.room.Room
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.wallet.data.JSMethod
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.RoomMigrations
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.SendTransactionData
import com.dimension.maskbook.wallet.handler.Web3MessageHandler
import com.dimension.maskbook.wallet.model.SendTokenRequest
import com.dimension.maskbook.wallet.repository.CollectibleRepository
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.ITokenRepository
import com.dimension.maskbook.wallet.repository.ITransactionRepository
import com.dimension.maskbook.wallet.repository.IWalletConnectRepository
import com.dimension.maskbook.wallet.repository.IWalletContactRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.SendHistoryRepository
import com.dimension.maskbook.wallet.repository.TokenRepository
import com.dimension.maskbook.wallet.repository.TransactionRepository
import com.dimension.maskbook.wallet.repository.WalletConnectRepository
import com.dimension.maskbook.wallet.repository.WalletContactRepository
import com.dimension.maskbook.wallet.repository.WalletRepository
import com.dimension.maskbook.wallet.repository.walletDataStore
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.route.generatedRoute
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.ui.scenes.register.createidentity.createIdentityRoute
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.local.recoveryLocalRoute
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.create.createWalletRoute
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.transferRoute
import com.dimension.maskbook.wallet.ui.tab.WalletTabScreen
import com.dimension.maskbook.wallet.usecase.AddContactUseCase
import com.dimension.maskbook.wallet.usecase.AddRecentAddressUseCase
import com.dimension.maskbook.wallet.usecase.GetAddressUseCase
import com.dimension.maskbook.wallet.usecase.GetArrivesWithGasFeeUseCase
import com.dimension.maskbook.wallet.usecase.GetContactsUseCase
import com.dimension.maskbook.wallet.usecase.GetEnsAddressUseCase
import com.dimension.maskbook.wallet.usecase.GetRecentAddressUseCase
import com.dimension.maskbook.wallet.usecase.GetSuggestGasFeeUseCase
import com.dimension.maskbook.wallet.usecase.GetWalletCollectibleCollectionsUseCase
import com.dimension.maskbook.wallet.usecase.GetWalletCollectibleUseCase
import com.dimension.maskbook.wallet.usecase.GetWalletNativeTokenUseCase
import com.dimension.maskbook.wallet.usecase.GetWalletTokenByAddressUseCase
import com.dimension.maskbook.wallet.usecase.GetWalletTokensUseCase
import com.dimension.maskbook.wallet.usecase.RefreshWalletUseCase
import com.dimension.maskbook.wallet.usecase.SendTokenUseCase
import com.dimension.maskbook.wallet.usecase.SendTransactionUseCase
import com.dimension.maskbook.wallet.usecase.SendWalletCollectibleUseCase
import com.dimension.maskbook.wallet.usecase.SetCurrentChainUseCase
import com.dimension.maskbook.wallet.usecase.VerifyPaymentPasswordUseCase
import com.dimension.maskbook.wallet.viewmodel.WelcomeViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.PrivateKeyViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.RecoveryLocalViewModel
import com.dimension.maskbook.wallet.viewmodel.register.CreateIdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.register.EmailRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.PhoneRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.RemoteBackupRecoveryViewModelBase
import com.dimension.maskbook.wallet.viewmodel.wallets.BackUpPasswordViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.BiometricViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.SetUpPaymentPasswordViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.TokenDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.TouchIdEnableViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.UnlockWalletViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletBalancesViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletConnectManagementViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletManagementModalViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.collectible.CollectibleDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.collectible.CollectiblesViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.create.CreateWalletRecoveryKeyViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletDerivationPathViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletKeystoreViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletMnemonicViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletPrivateKeyViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletDeleteViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletRenameViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletSwitchEditViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletSwitchViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletTransactionHistoryViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.AddContactViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.GasFeeViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SearchAddressViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SearchTradableViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SendConfirmViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.TransferDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.Web3TransactionConfirmViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.walletconnect.WalletConnectResult
import com.dimension.maskbook.wallet.viewmodel.wallets.walletconnect.WalletConnectViewModel
import com.dimension.maskbook.wallet.walletconnect.WalletConnectClientManager
import com.dimension.maskbook.wallet.walletconnect.WalletConnectServerManager
import com.dimension.maskbook.wallet.walletconnect.v1.client.WalletConnectClientManagerV1
import com.dimension.maskbook.wallet.walletconnect.v1.server.WalletConnectServerManagerV1
import com.google.accompanist.navigation.animation.navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import com.dimension.maskbook.wallet.export.WalletServices as ExportWalletServices

object WalletSetup : ModuleSetup {

    @OptIn(ExperimentalAnimationApi::class)
    override fun NavGraphBuilder.route(navController: NavController) {
        generatedRoute(navController)
        navigation(
            startDestination = WalletRoute.Transfer.SearchAddress.path,
            route = WalletRoute.Transfer.Route
        ) {
            transferRoute(navController)
        }
        navigation(
            startDestination = WalletRoute.Register.CreateIdentity.Backup.path,
            route = WalletRoute.Register.CreateIdentity.Route
        ) {
            createIdentityRoute(navController)
        }
        navigation(
            startDestination = WalletRoute.CreateWallet.Pharse.path,
            route = WalletRoute.CreateWallet.Route
        ) {
            createWalletRoute(navController)
        }
        navigation(
            startDestination = WalletRoute.Register.Recovery.LocalBackup.Loading.path,
            route = WalletRoute.Register.Recovery.LocalBackup.Route,
        ) {
            recoveryLocalRoute(navController)
        }
    }

    override fun dependencyInject() = module {
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

        single<ExportWalletServices> { WalletServicesImpl(get()) }

        single { WalletTabScreen() } bind TabScreen::class

        provideRepository()
        provideUseCase()
        provideViewModel()
        provideServices()
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
                        val platform =
                            CoinPlatformType.values().firstOrNull { it.coinId == data.coinId }
                        if (platform != null) {
                            get<IWalletRepository>().setActiveCoinPlatformType(platform)
                        }
                    }
                    if (data.networkId != null) {
                        val chainType =
                            ChainType.values().firstOrNull { it.chainId == data.networkId }
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

private fun Module.provideRepository() {
    single<WalletConnectClientManager> {
        // V2 SDK support only provides the Responder implementation at the Beta stage
        WalletConnectClientManagerV1(get())
    }
    single<WalletConnectServerManager> {
        WalletConnectServerManagerV1(get())
    }
    single<IWalletRepository> {
        WalletRepository(
            get<Context>().walletDataStore,
            get(),
            get(),
            get(),
            get()
        )
    }
    single { JSMethod(get()) }
    single { Web3MessageHandler(get()) }
    single<ICollectibleRepository> { CollectibleRepository(get(), get()) }
    single<ITransactionRepository> { TransactionRepository(get(), get()) }
    single<ITokenRepository> { TokenRepository(get()) }
    single<ISendHistoryRepository> { SendHistoryRepository(get()) }
    single<IWalletContactRepository> { WalletContactRepository(get()) }
    single<IWalletConnectRepository> { WalletConnectRepository(get(), get()) }
}

private fun Module.provideUseCase() {
    // address
    factory { GetEnsAddressUseCase(get()) }
    factory { GetRecentAddressUseCase(get()) }
    factory { GetContactsUseCase(get()) }
    factory { GetAddressUseCase(get()) }
    factory { AddRecentAddressUseCase(get()) }
    factory { AddContactUseCase(get()) }

    // chain
    factory { SetCurrentChainUseCase(get()) }
    // general
    factory { VerifyPaymentPasswordUseCase(get()) }
    // collectible
    factory {
        GetWalletCollectibleCollectionsUseCase(
            get(),
            get()
        )
    }
    factory { GetWalletCollectibleUseCase(get()) }
    factory { SendWalletCollectibleUseCase(get()) }
    // Tokens
    factory { GetWalletNativeTokenUseCase(get()) }
    factory { GetWalletTokenByAddressUseCase(get()) }
    factory { GetWalletTokensUseCase(get()) }
    factory { SendTokenUseCase(get()) }
    factory { SendTransactionUseCase(get()) }
    // gas
    factory { GetArrivesWithGasFeeUseCase(get()) }
    factory { GetSuggestGasFeeUseCase(get()) }
    // wallet
    factory { RefreshWalletUseCase(get()) }
}

private fun Module.provideViewModel() {
    viewModel { (uri: String, account: String?) ->
        RecoveryLocalViewModel(
            get(),
            uri,
            account,
            get<Context>().contentResolver,
            get()
        )
    }
    viewModel { (name: String) -> IdentityViewModel(get(), get(), name) }
    viewModel { PrivateKeyViewModel(get(), get()) }
    viewModel { (personaName: String) -> CreateIdentityViewModel(personaName, get(), get()) }
    viewModel { WelcomeViewModel(get()) }
    viewModel { (requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit) ->
        EmailRemoteBackupRecoveryViewModel(
            requestNavigate,
            get(),
        )
    }
    viewModel { (requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit) ->
        PhoneRemoteBackupRecoveryViewModel(
            requestNavigate,
            get()
        )
    }
    viewModel { (wallet: String) -> CreateWalletRecoveryKeyViewModel(wallet, get()) }
    viewModel { SetUpPaymentPasswordViewModel(get()) }
    viewModel { TouchIdEnableViewModel() }
    viewModel { (wallet: String) -> ImportWalletKeystoreViewModel(wallet, get()) }
    viewModel { (wallet: String) -> ImportWalletPrivateKeyViewModel(wallet, get()) }
    viewModel { (wallet: String) -> ImportWalletMnemonicViewModel(wallet, get()) }
    viewModel { (wallet: String, mnemonicCode: List<String>) ->
        ImportWalletDerivationPathViewModel(
            wallet,
            mnemonicCode,
            get()
        )
    }
    viewModel { WalletTransactionHistoryViewModel(get(), get()) }
    viewModel { (id: String, name: String) -> WalletRenameViewModel(id, name, get()) }
    viewModel { WalletBalancesViewModel(get(), get(), get()) }
    viewModel { WalletManagementModalViewModel(get()) }
    viewModel { WalletBackupViewModel(get(), get()) }
    viewModel { (id: String) -> WalletDeleteViewModel(id, get(), get()) }
    viewModel { WalletSwitchViewModel(get()) }
    viewModel { SearchAddressViewModel(get(), get(), get(), get()) }
    viewModel { (id: String) -> TokenDetailViewModel(id, get(), get(), get()) }
    viewModel { (initialGasLimit: Double) ->
        GasFeeViewModel(
            initialGasLimit = initialGasLimit,
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel { (tradableId: String) ->
        TransferDetailViewModel(
            tradableId = tradableId,
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel { AddContactViewModel(get()) }
    viewModel { (toAddress: String) ->
        SendConfirmViewModel(toAddress, get(), get(), get(), get())
    }
    viewModel { BiometricViewModel(get(), get()) }
    viewModel { WalletConnectManagementViewModel(get(), get()) }
    viewModel { (onResult: (WalletConnectResult) -> Unit) ->
        WalletConnectViewModel(
            get(),
            get(),
            get(),
            get<Context>().packageManager,
            onResult
        )
    }
    viewModel { UnlockWalletViewModel(get(), get()) }
    viewModel { BackUpPasswordViewModel(get(), get()) }
    viewModel { (id: String) -> CollectibleDetailViewModel(id, get(), get(), get()) }
    viewModel { CollectiblesViewModel(get(), get()) }
    viewModel { (data: SendTransactionData, request: SendTokenRequest?) ->
        Web3TransactionConfirmViewModel(
            data,
            request,
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { SearchTradableViewModel(get(), get()) }
    viewModel { (id: String) -> WalletSwitchEditViewModel(id, get()) }
}

private fun Module.provideServices() {
    single { WalletServices(get()) }
}
