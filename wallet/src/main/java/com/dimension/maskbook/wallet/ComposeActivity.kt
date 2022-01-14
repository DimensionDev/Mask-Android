package com.dimension.maskbook.wallet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.room.Room
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.repository.TokenData
import com.dimension.maskbook.wallet.services.DownloadResponse
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.Route
import com.dimension.maskbook.wallet.utils.BiometricAuthenticator
import com.dimension.maskbook.wallet.viewmodel.WelcomeViewModel
import com.dimension.maskbook.wallet.viewmodel.app.LabsViewModel
import com.dimension.maskbook.wallet.viewmodel.app.MarketTrendSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.app.PluginSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.ExportPrivateKeyViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.PersonaViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.RenamePersonaViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.SwitchPersonaViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.contacts.ContactsViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.post.PostViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.DisconnectSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.FaceBookConnectSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.FacebookSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.PersonaSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.SocialViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.TwitterConnectSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.TwitterSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.PrivateKeyViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.RecoveryLocalViewModel
import com.dimension.maskbook.wallet.viewmodel.register.CreateIdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.register.EmailRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.PhoneRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.RemoteBackupRecoveryViewModelBase
import com.dimension.maskbook.wallet.viewmodel.register.UserNameModalViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.AppearanceSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupCloudExecuteViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupCloudViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupLocalViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupMergeConfirmViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupPasswordSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.DataSourceSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.EmailBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.EmailSetupViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.LanguageSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.PaymentPasswordSettingsViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.PhoneBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.PhoneSetupViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.*
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
import com.dimension.maskbook.wallet.viewmodel.wallets.send.SendTokenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class ComposeActivity : AppCompatActivity() {
    companion object {
        object Destination {
            val register = "Register"
            val main = "Main"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startDestination = intent.getStringExtra("startDestination") ?: Destination.register
        setContent {
            MaskTheme {
                App(
                    onBack = {
                        finish()
                    },
                    startDestination = startDestination,
                )
            }
        }
    }
}

@Composable
fun App(
    startDestination: String = "Register",
    onBack: () -> Unit,
) {
    Route(onBack = onBack, startDestination = startDestination)
}

val walletModules = module {
//    single<IPersonaRepository> { FakePersonaRepository() }
//    single<IPostRepository> { FakePostRepository() }
//    single<IContactsRepository> { FakeContactsRepository() }
//    single<IAppRepository> { FakeAppRepository() }
//    single<IWalletRepository> { FakeWalletRepository() }
//    single<ISettingsRepository> { FakeSettingsRepository() }

    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "maskbook")
            .setQueryExecutor(Dispatchers.IO.asExecutor())
            .setTransactionExecutor(Dispatchers.IO.asExecutor())
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        BiometricAuthenticator()
    }

    viewModel { (uri: Uri) -> RecoveryLocalViewModel(get(), uri, get<Context>().contentResolver) }
    viewModel { IdentityViewModel(get()) }
    viewModel { PrivateKeyViewModel(get()) }
    viewModel { CreateIdentityViewModel(get()) }
    viewModel { PersonaViewModel(get()) }
    viewModel { SocialViewModel(get()) }
    viewModel { TwitterSocialViewModel(get()) }
    viewModel { FacebookSocialViewModel(get()) }
    viewModel { WelcomeViewModel(get()) }
    viewModel { TwitterConnectSocialViewModel(get()) }
    viewModel { FaceBookConnectSocialViewModel(get()) }
    viewModel { DisconnectSocialViewModel(get()) }
    viewModel { SwitchPersonaViewModel(get()) }
    viewModel { (personaId: String) -> RenamePersonaViewModel(get(), personaId) }
    viewModel { PersonaSocialViewModel(get()) }
    viewModel { ExportPrivateKeyViewModel(get()) }
    viewModel { PostViewModel(get(), get()) }
    viewModel { ContactsViewModel(get(), get()) }
    viewModel { LabsViewModel(get(), get()) }
    viewModel { PluginSettingsViewModel(get(), get()) }
    viewModel { LanguageSettingsViewModel(get()) }
    viewModel { AppearanceSettingsViewModel(get()) }
    viewModel { DataSourceSettingsViewModel(get()) }
    viewModel { PaymentPasswordSettingsViewModel(get()) }
    viewModel { BackupPasswordSettingsViewModel(get()) }
    viewModel { BackupLocalViewModel(get(), get()) }
    viewModel { MarketTrendSettingsViewModel(get()) }
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
    viewModel { (requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit) ->
        EmailSetupViewModel(
            requestNavigate = requestNavigate,
            backupRepository = get(),
            personaRepository = get(),
        )
    }
    viewModel { (requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit) ->
        PhoneSetupViewModel(
            requestNavigate = requestNavigate,
            backupRepository = get(),
            personaRepository = get()
        )
    }
    viewModel { (
                    requestMerge: (target: DownloadResponse, email: String, code: String) -> Unit,
                    next: (email: String, code: String) -> Unit,
                ) ->
        EmailBackupViewModel(get(), requestMerge, next)
    }
    viewModel { (
                    requestMerge: (target: DownloadResponse, email: String, code: String) -> Unit,
                    next: (email: String, code: String) -> Unit,
                ) ->
        PhoneBackupViewModel(get(), requestMerge, next)
    }
    viewModel { (onDone: () -> Unit) ->
        BackupMergeConfirmViewModel(get(), get(), onDone)
    }
    viewModel { UserNameModalViewModel(get()) }
    viewModel { BackupCloudViewModel(get()) }
    viewModel { BackupCloudExecuteViewModel(get(), get(), get()) }
    viewModel { CreateWalletRecoveryKeyViewModel(get()) }
    viewModel { SetUpPaymentPasswordViewModel(get()) }
    viewModel { BiometricEnableViewModel(get(), get()) }
    viewModel { TouchIdEnableViewModel() }
    viewModel { (wallet: String) -> ImportWalletKeystoreViewModel(wallet, get()) }
    viewModel { (wallet: String) -> ImportWalletPrivateKeyViewModel(wallet, get()) }
    viewModel { (wallet: String) -> ImportWalletMnemonicViewModel(wallet) }
    viewModel { (wallet: String, mnemonicCode: List<String>) ->
        ImportWalletDerivationPathViewModel(
            wallet,
            mnemonicCode,
            get()
        )
    }
    viewModel { WalletTransactionHistoryViewModel(get(), get()) }
    viewModel { (id: String) -> WalletRenameViewModel(id, get()) }
    viewModel { WalletBalancesViewModel(get()) }
    viewModel { WalletManagementModalViewModel(get()) }
    viewModel { WalletBackupViewModel(get()) }
    viewModel { (id: String) -> WalletDeleteViewModel(id, get(), get()) }
    viewModel { WalletSwitchViewModel(get()) }
    viewModel { SearchAddressViewModel(get(), get(), get()) }
    viewModel { (id: String) -> TokenDetailViewModel(id, get(), get(), get()) }
    viewModel { (initialGasLimit: Double) -> GasFeeViewModel(initialGasLimit = initialGasLimit, get(), get()) }
    viewModel { (tokenData: TokenData, toAddress: String) ->
        SendTokenViewModel(
            tokenData = tokenData,
            toAddress = toAddress,
            get(),
            get(),
            get(),
        )
    }
    viewModel { AddContactViewModel(get()) }
    viewModel { (tokenData: TokenData, toAddress: String) ->
        SendConfirmViewModel(tokenData, toAddress, get(), get())
    }
    viewModel { BiometricViewModel(get(), get()) }
    viewModel { UnlockWalletViewModel(get(), get()) }
    viewModel { BackUpPasswordViewModel(get(), get()) }
}

val servicesModule = module {
    single { WalletServices() }
}