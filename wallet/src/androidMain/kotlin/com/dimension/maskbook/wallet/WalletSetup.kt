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
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import androidx.room.Room
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.persona.export.model.ConnectAccountData
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.RoomMigrations
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
import com.dimension.maskbook.wallet.route.registerRoute
import com.dimension.maskbook.wallet.route.walletsRoute
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.ui.scenes.persona.BackUpPasswordModal
import com.dimension.maskbook.wallet.ui.tab.WalletTabScreen
import com.dimension.maskbook.wallet.viewmodel.WelcomeViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.PrivateKeyViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.RecoveryLocalViewModel
import com.dimension.maskbook.wallet.viewmodel.register.CreateIdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.register.EmailRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.PhoneRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.RemoteBackupRecoveryViewModelBase
import com.dimension.maskbook.wallet.viewmodel.register.UserNameModalViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.BackUpPasswordViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.BiometricViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.SetUpPaymentPasswordViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.TokenDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.TouchIdEnableViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.UnlockWalletViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletBalancesViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletConnectManagementViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletConnectViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletManagementModalViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.collectible.CollectibleDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.create.CreateWalletRecoveryKeyViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletDerivationPathViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletKeystoreViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletMnemonicViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletPrivateKeyViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletDeleteViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletRenameViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletSwitchViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletTransactionHistoryViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.AddContactViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.GasFeeViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SearchAddressViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SendConfirmViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SendTokenDataViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SendTokenViewModel
import com.dimension.maskbook.wallet.walletconnect.WalletConnectClientManager
import com.dimension.maskbook.wallet.walletconnect.WalletConnectServerManager
import com.dimension.maskbook.wallet.walletconnect.v1.client.WalletConnectClientManagerV1
import com.dimension.maskbook.wallet.walletconnect.v1.server.WalletConnectServerManagerV1
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import com.dimension.maskbook.wallet.export.WalletServices as ExportWalletServices

object WalletSetup : ModuleSetup {

    @OptIn(ExperimentalMaterialNavigationApi::class)
    override fun NavGraphBuilder.route(navController: NavController, onBack: () -> Unit) {
        walletsRoute(navController)
        registerRoute(navController)
        bottomSheet(
            WalletRoute.BackUpPassword.path,
            arguments = listOf(
                navArgument("target") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = Deeplinks.Wallet.BackUpPassword.path
                }
            )
        ) { backStackEntry ->
            val target = backStackEntry.arguments?.getString("target")
            val viewModel = getViewModel<BackUpPasswordViewModel>()
            val biometricEnable by viewModel.biometricEnabled.observeAsState(initial = false)
            val password by viewModel.password.observeAsState(initial = "")
            val passwordValid by viewModel.passwordValid.observeAsState(initial = false)
            val context = LocalContext.current
            BackUpPasswordModal(
                biometricEnabled = biometricEnable,
                password = password,
                onPasswordChanged = { viewModel.setPassword(it) },
                passwordValid = passwordValid,
                onConfirm = {
                    if (biometricEnable) {
                        viewModel.authenticate(
                            context = context,
                            onSuccess = {
                                target?.let {
                                    navController.navigate(
                                        it,
                                        navOptions {
                                            popUpTo(WalletRoute.BackUpPassword.path) {
                                                inclusive = true
                                            }
                                        }
                                    )
                                } ?: navController.popBackStack()
                            }
                        )
                    } else {
                        target?.let {
                            navController.navigate(
                                it,
                                navOptions {
                                    popUpTo(WalletRoute.BackUpPassword.path) {
                                        inclusive = true
                                    }
                                }
                            )
                        } ?: navController.popBackStack()
                    }
                }
            )
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
                )
                .build()
        }

        single<ExportWalletServices> { WalletServicesImpl(get()) }

        single { WalletTabScreen() } bind TabScreen::class

        provideRepository()
        provideViewModel()
        provideServices()
    }

    override fun onExtensionReady() {
        initRepository()
        initWalletConnect()
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
        .init { clientMeta, request ->
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
    single<IWalletRepository> { WalletRepository(get<Context>().walletDataStore, get(), get(), get()) }
    single<ICollectibleRepository> { CollectibleRepository(get(), get()) }
    single<ITransactionRepository> { TransactionRepository(get(), get()) }
    single<ITokenRepository> { TokenRepository(get()) }
    single<ISendHistoryRepository> { SendHistoryRepository(get()) }
    single<IWalletContactRepository> { WalletContactRepository(get()) }
    single<IWalletConnectRepository> { WalletConnectRepository(get(), get()) }
}

private fun Module.provideViewModel() {
    viewModel { (uri: Uri) -> RecoveryLocalViewModel(get(), uri, get<Context>().contentResolver) }
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
    viewModel { (data: ConnectAccountData) -> UserNameModalViewModel(get(), data) }
    viewModel { CreateWalletRecoveryKeyViewModel(get()) }
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
    viewModel { WalletBalancesViewModel(get(), get()) }
    viewModel { WalletManagementModalViewModel(get()) }
    viewModel { WalletBackupViewModel(get(), get()) }
    viewModel { (id: String) -> WalletDeleteViewModel(id, get(), get()) }
    viewModel { WalletSwitchViewModel(get()) }
    viewModel { SearchAddressViewModel(get(), get(), get()) }
    viewModel { (id: String) -> TokenDetailViewModel(id, get(), get(), get()) }
    viewModel { (initialGasLimit: Double) ->
        GasFeeViewModel(
            initialGasLimit = initialGasLimit,
            get(),
            get()
        )
    }
    viewModel { (toAddress: String) ->
        SendTokenViewModel(
            toAddress = toAddress,
            get(),
            get(),
        )
    }
    viewModel { AddContactViewModel(get()) }
    viewModel { (toAddress: String) ->
        SendConfirmViewModel(toAddress, get(), get())
    }
    viewModel { BiometricViewModel(get(), get()) }
    viewModel { WalletConnectManagementViewModel(get(), get()) }
    viewModel { (onResult: (success: Boolean, needToSwitchNetwork: Boolean) -> Unit) ->
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
    viewModel { (id: String) -> CollectibleDetailViewModel(id, get()) }
    viewModel { (tokenAddress: String) -> SendTokenDataViewModel(tokenAddress, get(), get()) }
}

private fun Module.provideServices() {
    single { WalletServices(get()) }
}
