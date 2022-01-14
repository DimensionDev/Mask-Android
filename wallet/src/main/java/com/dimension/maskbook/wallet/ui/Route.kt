package com.dimension.maskbook.wallet.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import androidx.navigation.plusAssign
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.AppKey
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.ITokenRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.PlatformType
import com.dimension.maskbook.wallet.route.backup
import com.dimension.maskbook.wallet.ui.scenes.MainHost
import com.dimension.maskbook.wallet.ui.scenes.app.PluginSettingsScene
import com.dimension.maskbook.wallet.ui.scenes.app.settings.MarketTrendSettingsModal
import com.dimension.maskbook.wallet.ui.scenes.persona.*
import com.dimension.maskbook.wallet.ui.scenes.persona.social.ConnectSocialModal
import com.dimension.maskbook.wallet.ui.scenes.persona.social.DisconnectSocialDialog
import com.dimension.maskbook.wallet.ui.scenes.register.RegisterScene
import com.dimension.maskbook.wallet.ui.scenes.register.createidentity.CreateIdentityHost
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.IdentityScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.PrivateKeyScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryComplectedScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryHomeScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.local.RecoveryLocalHost
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.remote.remoteBackupRecovery
import com.dimension.maskbook.wallet.ui.scenes.settings.AppearanceSettings
import com.dimension.maskbook.wallet.ui.scenes.settings.BackupPasswordSettings
import com.dimension.maskbook.wallet.ui.scenes.settings.DataSourceSettings
import com.dimension.maskbook.wallet.ui.scenes.settings.LanguageSettings
import com.dimension.maskbook.wallet.ui.scenes.settings.PaymentPasswordSettings
import com.dimension.maskbook.wallet.ui.scenes.wallets.UnlockWalletDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.WalletQrcodeScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.MultiChainWalletDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.CreateOrImportWalletScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.CreateType
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.create.CreateWalletHost
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.import.ImportWalletHost
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.LegalScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password.BiometricsEnableScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password.SetUpPaymentPassword
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password.TouchIdEnableScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.BackupWalletScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletConnectModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletDeleteDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletManagementModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletRenameModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletSwitchAddModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletSwitchModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletSwitchScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletTransactionHistoryScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.SendTokenHost
import com.dimension.maskbook.wallet.ui.scenes.wallets.token.TokenDetailScene
import com.dimension.maskbook.wallet.ui.widget.EmailCodeInputModal
import com.dimension.maskbook.wallet.ui.widget.EmailInputModal
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PhoneCodeInputModal
import com.dimension.maskbook.wallet.ui.widget.PhoneInputModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.viewmodel.persona.RenamePersonaViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.SwitchPersonaViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.DisconnectSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.PrivateKeyViewModel
import com.dimension.maskbook.wallet.viewmodel.register.RemoteBackupRecoveryViewModelBase
import com.dimension.maskbook.wallet.viewmodel.settings.EmailSetupViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.PhoneSetupViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.BiometricViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.TokenDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.UnlockWalletViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletManagementModalViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletDeleteViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletRenameViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletSwitchViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletTransactionHistoryViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

val LocalRootNavController =
    staticCompositionLocalOf<NavHostController> { error("No NavHostController") }

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun Route(
    startDestination: String = "Register",
    onBack: () -> Unit,
) {
    val navController = rememberAnimatedNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    CompositionLocalProvider(LocalRootNavController provides navController) {
        ModalBottomSheetLayout(
            bottomSheetNavigator,
            sheetBackgroundColor = MaterialTheme.colors.background,
        ) {
            AnimatedNavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = { _, _ ->
                    slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween())
                },
                exitTransition = { _, _ ->
                    slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween())
                },
                popEnterTransition = { _, _ ->
                    slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween())
                },
                popExitTransition = { _, _ ->
                    slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween())
                },
            ) {
                navigation(
                    route = "Register",
                    startDestination = "Init",
                ) {
                    composable(
                        "Init",
                    ) {
                        val repository = get<IPersonaRepository>()
                        val persona by repository.currentPersona.observeAsState(initial = null)
                        LaunchedEffect(Unit) {
                            snapshotFlow { persona }
                                .distinctUntilChanged()
                                .collect {
                                    if (it != null) {
                                        navController.navigate("Main") {
                                            popUpTo("Register") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                        }
                        RegisterScene(
                            onCreateIdentity = {
                                navController.navigate("CreateIdentity")
                            },
                            onRecoveryAndSignIn = {
                                navController.navigate("Recovery")
                            },
                            onSynchronization = {

                            },
                        )
                    }
                    composable("CreateIdentity") {
                        CreateIdentityHost(
                            onDone = {
                                navController.navigate("Main") {
                                    popUpTo("Register") {
                                        inclusive = true
                                    }
                                }
                            },
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    navigation(startDestination = "Home", route = "Recovery") {
                        composable("Home") {
                            RecoveryHomeScene(
                                onBack = {
                                    navController.popBackStack()
                                },
                                onIdentity = {
                                    navController.navigate("Identity")
                                },
                                onPrivateKey = {
                                    navController.navigate("PrivateKey")
                                },
                                onLocalBackup = {
                                    navController.navigate("LocalBackup")
                                },
                                onRemoteBackup = {
                                    navController.navigate("RemoteBackupRecovery")
                                }
                            )
                        }
                        navigation("RemoteBackupRecovery_Email", "RemoteBackupRecovery") {
                            remoteBackupRecovery(navController)
                        }
                        navigation("LocalBackup_PickFile", "LocalBackup") {
                            composable(
                                "RemoteBackupRecovery_RecoveryLocal/{uri}",
                                arguments = listOf(
                                    navArgument("uri") { type = NavType.StringType },
                                )
                            ) {
                                val uri = it.arguments?.getString("uri")
                                    ?.let { Uri.parse(it) }
                                if (uri != null) {
                                    RecoveryLocalHost(
                                        uri = uri,
                                        onBack = {
                                            navController.popBackStack()
                                        },
                                        onConfirm = {
                                            navController.navigate("Complected") {
                                                popUpTo("Init") {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            composable("LocalBackup_PickFile") {
                                val filePickerLauncher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.OpenDocument(),
                                    onResult = {
                                        if (it != null) {
                                            navController.navigate(
                                                "RemoteBackupRecovery_RecoveryLocal/${
                                                    it.toString().encodeUrl()
                                                }"
                                            ) {
                                                popUpTo("LocalBackup_PickFile") {
                                                    inclusive = true
                                                }
                                            }
                                        } else {
                                            navController.popBackStack()
                                        }
                                    },
                                )
                                LaunchedEffect(Unit) {
                                    filePickerLauncher.launch(arrayOf("*/*"))
                                }
                            }
                        }
                        composable("Identity") {
                            val viewModel: IdentityViewModel = getViewModel()
                            val identity by viewModel.identity.observeAsState(initial = "")
                            IdentityScene(
                                identity = identity,
                                onIdentityChanged = {
                                    viewModel.setIdentity(it)
                                },
                                onConfirm = {
                                    viewModel.onConfirm()
                                    navController.navigate("Complected") {
                                        popUpTo("Init") {
                                            inclusive = true
                                        }
                                    }
                                },
                                onBack = {
                                    navController.popBackStack()
                                },
                            )
                        }
                        composable("PrivateKey") {
                            val viewModel: PrivateKeyViewModel = getViewModel()
                            val privateKey by viewModel.privateKey.observeAsState(initial = "")
                            PrivateKeyScene(
                                privateKey = privateKey,
                                onPrivateKeyChanged = {
                                    viewModel.setPrivateKey(it)
                                },
                                onConfirm = {
                                    viewModel.onConfirm()
                                    navController.navigate("Complected") {
                                        popUpTo("Init") {
                                            inclusive = true
                                        }
                                    }
                                },
                                onBack = {
                                    navController.popBackStack()
                                },
                            )
                        }
                        composable("Complected") {
                            RecoveryComplectedScene(
                                onBack = {
                                    navController.popBackStack()
                                },
                                onConfirm = {
                                    navController.navigate("Main") {
                                        popUpTo("Register") {
                                            inclusive = true
                                        }
                                    }
                                },
                            )
                        }

                    }
//                    composable("Welcome") {
//                        val viewModel: WelcomeViewModel = getViewModel()
//                        val persona by viewModel.persona.observeAsState(initial = "")
//                        WelcomeScene(
//                            persona = persona,
//                            onPersonaChanged = {
//                                viewModel.setPersona(it)
//                            },
//                            onNext = {
//                                viewModel.onConfirm()
//                                navController.navigate("Main") {
//                                    popUpTo("Register") {
//                                        inclusive = true
//                                    }
//                                }
//                            },
//                            onBack = {
//                                navController.popBackStack()
//                            }
//                        )
//                    }
                }
                navigation(route = "Main", startDestination = "Home") {
                    composable(
                        "Home",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "maskwallet://Home/{tab}"
                            }
                        ),
                        arguments = listOf(
                            navArgument("tab") { type = NavType.StringType }
                        )
                    ) {
                        MainHost(
                            initialTab = it.arguments?.getString("tab").orEmpty(),
                            onBack = onBack,
                            onLabsSettingClick = {
                                navController.navigate("PluginSettings")
                            },
                            onLabsItemClick = { appKey ->
                                when(appKey) {
                                    AppKey.Swap -> {
                                        navController.navigate("MarketTrendSettings")
                                    }
                                    else -> Unit
                                }
                            }
                        )
                    }
                    wallets(navController = navController)
                    settings(navController = navController)
                    composable("PluginSettings") {
                        PluginSettingsScene(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("ExportPrivateKeyScene") {
                        ExportPrivateKeyScene(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    dialog("Logout") {
                        LogoutDialog(
                            onBack = {
                                navController.popBackStack()
                            },
                            onDone = {
                                navController.popBackStack("Home", inclusive = false)
                            }
                        )
                    }
                    dialog("Delete") {
                        DeleteDialog(
                            onBack = {
                                navController.popBackStack("Home", inclusive = false)
                            }
                        )
                    }
                    bottomSheet(
                        "BackUpPassword/{target}",
                        arguments = listOf(
                            navArgument("target") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val target = backStackEntry.arguments?.getString("target")
                        val viewModel = getViewModel<UnlockWalletViewModel>()
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
                                                navController.navigate(it, navOptions {
                                                    popUpTo("BackUpPassword") {
                                                        inclusive = true
                                                    }
                                                })
                                            } ?: navController.popBackStack()
                                        }
                                    )
                                } else {
                                    target?.let {
                                        navController.navigate(it, navOptions {
                                            popUpTo("BackUpPassword") {
                                                inclusive = true
                                            }
                                        })
                                    } ?: navController.popBackStack()
                                }
                            }
                        )
                    }

                    bottomSheet("MarketTrendSettings") {
                        MarketTrendSettingsModal()
                    }
                    composable(
                        "PersonaMenu"
                    ) {
                        val persona by get<IPersonaRepository>().currentPersona.observeAsState(
                            initial = null
                        )
                        persona?.let {
                            PersonaMenu(
                                personaData = it,
                                navController = navController,
                                onBack = {
                                    navController.navigateUp()
                                }
                            )
                        }
                    }
                    bottomSheet("SwitchPersona") {
                        val viewModel = getViewModel<SwitchPersonaViewModel>()
                        val current by viewModel.current.observeAsState(initial = null)
                        val items by viewModel.items.observeAsState(initial = emptyList())

                        current?.let { it1 ->
                            SwitchPersonaModal(
                                currentPersonaData = it1,
                                items = items,
                                onAdd = {
                                    navController.navigate("CreatePersona")
                                },
                                onItemClicked = {
                                    viewModel.switch(it)
                                }
                            )
                        }
                    }
                    bottomSheet("CreatePersona") {
                        CreatePersona(
                            onDone = {
                                navController.popBackStack()
                            }
                        )
                    }
                    bottomSheet(
                        "RenamePersona/{personaId}",
                        arguments = listOf(
                            navArgument("personaId") { type = NavType.StringType },
                        )
                    ) {
                        val personaId = it.arguments?.getString("personaId")
                        if (personaId != null) {
                            val viewModel = getViewModel<RenamePersonaViewModel> {
                                parametersOf(personaId)
                            }
                            val name by viewModel.name.observeAsState(initial = "")
                            RenamePersona(
                                name = name,
                                onNameChanged = {
                                    viewModel.setName(it)
                                },
                                onDone = {
                                    viewModel.confirm()
                                    navController.popBackStack()
                                },
                            )
                        }
                    }
                    bottomSheet(
                        "ConnectSocial/{personaId}/{platform}",
                        arguments = listOf(
                            navArgument("personaId") { type = NavType.StringType },
                            navArgument("platform") { type = NavType.StringType },
                        ),
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "maskwallet://ConnectSocial/{personaId}/{platform}"
                            }
                        )
                    ) {
                        val personaId = it.arguments?.getString("personaId")
                        val platform = it.arguments?.getString("platform")
                            ?.let { PlatformType.valueOf(it) }
//                        val viewModel = when (platform) {
//                            PlatformType.Twitter -> getViewModel<TwitterConnectSocialViewModel>()
//                            PlatformType.Facebook -> getViewModel<FaceBookConnectSocialViewModel>()
//                            else -> null
//                        }
                        if (personaId != null && platform != null) {
                            val repository = get<IPersonaRepository>()
                            ConnectSocialModal(
                                onDone = {
                                    repository.beginConnectingProcess(
                                        personaId = personaId,
                                        platformType = platform,
                                    )
                                    onBack.invoke()
                                }
                            )
                        }
//                        if (viewModel != null && personaId != null && platform != null) {
//                            val items by viewModel.items.observeAsState(initial = emptyList())
//                            if (items.any()) {
//                                ConnectSocialModal(
//                                    onConnect = {
//                                        viewModel.connect(data = it, personaId = personaId)
//                                        navController.popBackStack()
//                                    },
//                                    socials = items
//                                )
//                            } else {
//                                val repository = get<IPersonaRepository>()
//                                ConnectSocialModal(
//                                    onDone = {
//                                        repository.beginConnectingProcess(
//                                            personaId = personaId,
//                                            platformType = platform,
//                                        )
//                                        onBack.invoke()
//                                    }
//                                )
//                            }
//                        }
                    }
                    dialog(
                        "DisconnectSocial/{personaId}/{platform}/{id}",
                        arguments = listOf(
                            navArgument("personaId") { type = NavType.StringType },
                            navArgument("platform") { type = NavType.StringType },
                            navArgument("id") { type = NavType.StringType },
                        )
                    ) {
                        val personaId = it.arguments?.getString("personaId")
                        val platform =
                            it.arguments?.getString("platform")?.let { PlatformType.valueOf(it) }
                        val id = it.arguments?.getString("id")
                        val viewModel = getViewModel<DisconnectSocialViewModel>()
                        if (personaId != null && platform != null && id != null) {
                            DisconnectSocialDialog(
                                onBack = {
                                    navController.popBackStack()
                                },
                                onConfirm = {
                                    when (platform) {
                                        PlatformType.Twitter ->
                                            viewModel.disconnectTwitter(
                                                personaId = personaId,
                                                socialId = id
                                            )
                                        PlatformType.Facebook ->
                                            viewModel.disconnectFacebook(
                                                personaId = personaId,
                                                socialId = id
                                            )
                                    }
                                    navController.popBackStack()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialNavigationApi
private fun NavGraphBuilder.wallets(
    navController: NavController
) {
    composable("WalletQrcode") {
        val repository = get<IWalletRepository>()
        val currentWallet by repository.currentWallet.observeAsState(initial = null)
        currentWallet?.let {
            WalletQrcodeScene(
                walletData = it,
                onShare = { /*TODO*/ },
                onBack = { navController.popBackStack() },
                onCopy = {},
            )
        }
    }
    composable(
        "TokenDetail/{id}",
        arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )
    ) {
        it.arguments?.getString("id")?.let { id ->
            val viewModel = getViewModel<TokenDetailViewModel> {
                parametersOf(id)
            }
            val token by viewModel.tokenData.observeAsState(initial = null)
            val transaction by viewModel.transaction.observeAsState(initial = emptyList())
            val walletTokenData by viewModel.walletTokenData.observeAsState(initial = null)
            walletTokenData?.let { walletTokenData ->
                token?.let { token ->
                    TokenDetailScene(
                        onBack = { navController.popBackStack() },
                        tokenData = token,
                        walletTokenData = walletTokenData,
                        transactions = transaction,
                        onSpeedUp = { },
                        onCancel = { },
                        onSend = {
                            navController.navigate("SendTokenScene/${token.address}")
                        }
                    )
                }
            }
        }
    }
    bottomSheet("SwitchWalletAdd") {
        WalletSwitchAddModal(
            onCreate = {
                navController.navigate("WalletIntroHostLegal/${CreateType.CREATE}")
            },
            onImport = {
                navController.navigate("WalletIntroHostLegal/${CreateType.IMPORT}")
            },
        )
    }
    bottomSheet("SwitchWalletAddWalletConnect") {
        WalletConnectModal()
    }
    bottomSheet("SwitchWallet") {
        val viewModel = getViewModel<WalletSwitchViewModel>()
        val wallet by viewModel.currentWallet.observeAsState(initial = null)
        val wallets by viewModel.wallets.observeAsState(initial = emptyList())
        val chainType by viewModel.network.observeAsState(initial = ChainType.eth)
        wallet?.let { it1 ->
            WalletSwitchScene(
                onBack = { navController.popBackStack() },
                selectedWallet = it1,
                wallets = wallets,
                onWalletSelected = {
                    viewModel.setCurrentWallet(it)
                },
                selectedChainType = chainType,
                onChainTypeSelected = {
                    viewModel.setChainType(it)
                },
                onAddWalletClicked = {
                    navController.navigate("SwitchWalletAdd")
                },
                onWalletConnectClicked = {
                    navController.navigate("SwitchWalletAddWalletConnect")
                },
                onEditMenuClicked = {
                    navController.navigate("WalletSwitchModal/${it.id}")
                }
            )
        }
    }
    bottomSheet(
        "WalletSwitchModal/{id}",
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) {
        it.arguments?.getString("id")?.let { id ->
            val repository = get<IWalletRepository>()
            val wallets by repository.wallets.observeAsState(initial = emptyList())
            wallets.firstOrNull { it.id == id }?.let { wallet ->
                WalletSwitchModal(
                    walletData = wallet,
                    onRename = { navController.navigate("WalletManagementRename/${wallet.id}") },
                    onDelete = {
                        navController.popBackStack()
                        navController.navigate("WalletManagementDeleteDialog/${wallet.id}")
                    },
                    onDisconnect = {
                    }
                )
            }
        }
    }
    bottomSheet("WalletBalancesMenu") {
        val viewModel = getViewModel<WalletManagementModalViewModel>()
        val currentWallet by viewModel.currentWallet.observeAsState(initial = null)
        currentWallet?.let { wallet ->
            WalletManagementModal(
                walletData = wallet,
                onRename = { navController.navigate("WalletManagementRename/${wallet.id}") },
                onBackup = { navController.navigate("UnlockWalletDialog/WalletManagementBackup") },
                onTransactionHistory = { navController.navigate("WalletManagementTransactionHistory") },
                onDelete = {
                    navController.popBackStack()
                    navController.navigate("WalletManagementDeleteDialog/${wallet.id}")
                },
                onDisconnect = {

                }
            )
        }
    }
    dialog(
        "WalletManagementDeleteDialog/{id}",
        arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )
    ) {
        it.arguments?.getString("id")?.let { id ->
            val viewModel = getViewModel<WalletDeleteViewModel> {
                parametersOf(id)
            }
            val biometricViewModel = getViewModel<BiometricViewModel>()
            val wallet by viewModel.wallet.observeAsState(initial = null)
            val biometricEnabled by biometricViewModel.biometricEnabled.observeAsState(initial = false)
            val context = LocalContext.current
            wallet?.let { walletData ->
                val password by viewModel.password.observeAsState(initial = "")
                val canConfirm by viewModel.canConfirm.observeAsState(initial = false)
                WalletDeleteDialog(
                    walletData = walletData,
                    password = password,
                    onPasswordChanged = { viewModel.setPassword(it) },
                    onBack = { navController.popBackStack() },
                    onDelete = {
                        if (biometricEnabled) {
                            biometricViewModel.authenticate(
                                context = context,
                                onSuccess = {
                                    viewModel.confirm()
                                    navController.popBackStack()
                                }
                            )
                        } else {
                            viewModel.confirm()
                            navController.popBackStack()
                        }
                    },
                    passwordValid = canConfirm,
                    biometricEnabled = biometricEnabled
                )
            }
        }
    }
    composable("WalletManagementBackup") {
        val viewModel = getViewModel<WalletBackupViewModel>()
        val keyStore by viewModel.keyStore.observeAsState(initial = "")
        val privateKey by viewModel.privateKey.observeAsState(initial = "")
        BackupWalletScene(
            keyStore = keyStore,
            privateKey = privateKey,
            onBack = { navController.popBackStack() },
        )
    }
    composable("WalletManagementTransactionHistory") {
        val viewModel = getViewModel<WalletTransactionHistoryViewModel>()
        val transaction by viewModel.transactions.observeAsState(initial = emptyList())
        WalletTransactionHistoryScene(
            onBack = { navController.popBackStack() },
            transactions = transaction,
            onSpeedUp = {
                // TODO:
            },
            onCancel = {
                // TODO:
            }
        )
    }
    bottomSheet(
        "WalletManagementRename/{id}",
        arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )
    ) {
        it.arguments?.getString("id")?.let { id ->
            val viewModel = getViewModel<WalletRenameViewModel> {
                parametersOf(id)
            }
            val name by viewModel.name.observeAsState(initial = "")
            WalletRenameModal(
                name = name,
                onNameChanged = { viewModel.setName(it) },
                onDone = {
                    viewModel.confirm()
                    navController.popBackStack()
                },
            )
        }
    }
    composable(
        "WalletIntroHostLegal/{type}",
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        val repo = get<ISettingsRepository>()
        val password by repo.paymentPassword.observeAsState(initial = null)
        val enableBiometric by repo.biometricEnabled.observeAsState(initial = false)
        LegalScene(
            onBack = { navController.popBackStack() },
            onAccept = {
                if (password.isNullOrEmpty()) {
                    navController.navigate("WalletIntroHostPassword/$type")
                } else if (!enableBiometric) {
                    navController.navigate("WalletIntroHostFaceId/$type")
                } else {
                    navController.navigate("CreateOrImportWallet/${type}")
                }
            },
            onBrowseAgreement = { TODO("Logic:browse service agreement") }
        )
    }

    bottomSheet(
        "WalletIntroHostPassword/{type}",
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        val enableBiometric by get<ISettingsRepository>().biometricEnabled.observeAsState(initial = false)
        SetUpPaymentPassword(
            onNext = {
                if (!enableBiometric) {
                    navController.navigate("WalletIntroHostFaceId/$type")
                } else {
                    navController.navigate("CreateOrImportWallet/${type}")
                }
            }
        )
    }

    composable(
        "WalletIntroHostFaceId/{type}",
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        BiometricsEnableScene(
            onBack = { navController.popBackStack() },
            onEnable = { enabled ->
                if (enabled) {
                    navController.navigate("WalletIntroHostFaceIdEnableSuccess/$type")
                } else {
                    navController.navigate("CreateOrImportWallet/${type}")
                }
            }
        )
    }

    dialog(
        "WalletIntroHostFaceIdEnableSuccess/{type}",
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        MaskDialog(
            onDismissRequest = {
                navController.navigate("CreateOrImportWallet/${type}")
            },
            title = {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_alert_biometry_id_activate_title))
            },
            text = {
                Text(text = "Face id has been enabled successfully.")
            },
            icon = {
                Image(
                    painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("CreateOrImportWallet/${type}")
                    },
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                }
            }
        )
    }

    composable(
        "WalletIntroHostTouchId/{type}",
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        TouchIdEnableScene(
            onBack = { navController.popBackStack() },
            onEnable = {
                navController.navigate("WalletIntroHostTouchIdEnableSuccess/$type")
            }
        )
    }

    dialog(
        "WalletIntroHostTouchIdEnableSuccess/{type}",
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        MaskDialog(
            onDismissRequest = {
                navController.navigate("CreateOrImportWallet/${type}")
            },
            title = {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_alert_biometry_id_activate_title))
            },
            text = {
                Text(text = "Touch id has been enabled successfully.")
            },
            icon = {
                Image(
                    painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("CreateOrImportWallet/${type}")
                    },
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                }
            }
        )
    }

    composable(
        "CreateOrImportWallet/{type}",
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        CreateOrImportWalletScene(
            onBack = { navController.popBackStack() },
            type = it.arguments?.getString("type")?.let { type ->
                CreateType.valueOf(type)
            } ?: CreateType.CREATE
        )
    }

    dialog("MultiChainWalletDialog") {
        MultiChainWalletDialog()
    }

    composable(
        "CreateWallet/{wallet}",
        arguments = listOf(
            navArgument("wallet") { type = NavType.StringType },
        )
    ) {
        it.arguments?.getString("wallet")?.let { wallet ->
            CreateWalletHost(
                wallet = wallet,
                onDone = {
                    navController.navigate(Uri.parse("maskwallet://Home/Wallets"), navOptions = navOptions {
                        launchSingleTop = true
                        popUpTo("Home") {
                            inclusive = false
                        }
                    })
                },
                onBack = { navController.popBackStack() }
            )
        }
    }

    composable(
        "ImportWallet/{wallet}",
        arguments = listOf(
            navArgument("wallet") { type = NavType.StringType },
        )
    ) {
        it.arguments?.getString("wallet")?.let { wallet ->
            ImportWalletHost(
                wallet = wallet,
                onDone = {
                    navController.navigate(Uri.parse("maskwallet://Home/Wallets"), navOptions = navOptions {
                        launchSingleTop = true
                        popUpTo("Home") {
                            inclusive = false
                        }
                    })
                },
                onBack = { navController.popBackStack() }
            )
        }
    }

    composable(
        "SendTokenScene/{token}",
        arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    ) {
        it.arguments?.getString("token")?.let { token ->
            val tokenRepository = get<ITokenRepository>()
            val tokenData by tokenRepository.getTokenByAddress(token).observeAsState(initial = null)
            tokenData?.let { it1 ->
                SendTokenHost(
                    it1,
                    onDone = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    dialog(
        "UnlockWalletDialog/{target}",
        arguments = listOf(
            navArgument("target") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val viewModel = getViewModel<UnlockWalletViewModel>()
        val biometricEnable by viewModel.biometricEnabled.observeAsState(initial = false)
        val password by viewModel.password.observeAsState(initial = "")
        val passwordValid by viewModel.passwordValid.observeAsState(initial = false)
        val context = LocalContext.current
        val target = backStackEntry.arguments?.getString("target")
        UnlockWalletDialog(
            onBack = { navController.popBackStack() },
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
                                navController.navigate(it, navOptions {
                                    popUpTo("UnlockWalletDialog") {
                                        inclusive = true
                                    }
                                })
                            } ?: navController.popBackStack()
                        }
                    )
                } else {
                    target?.let {
                        navController.navigate(it, navOptions {
                            popUpTo("UnlockWalletDialog") {
                                inclusive = true
                            }
                        })
                    } ?: navController.popBackStack()
                }
            }
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialNavigationApi
private fun NavGraphBuilder.settings(
    navController: NavController
) {
    dialog("SetupPasswordDialog") {
        MaskDialog(
            onDismissRequest = {
                navController.popBackStack()
            },
            title = {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_alert_setting_warning_backup_data_titile))
            },
            text = {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_alert_setting_warning_backup_data_description))
            },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text(text = "OK")
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_property_1_note),
                    contentDescription = null
                )
            }
        )
    }
    bottomSheet("LanguageSettings") {
        LanguageSettings(
            onBack = {
                navController.popBackStack()
            }
        )
    }
    bottomSheet("AppearanceSettings") {
        AppearanceSettings(
            onBack = {
                navController.popBackStack()
            }
        )
    }
    bottomSheet("DataSourceSettings") {
        DataSourceSettings(
            onBack = {
                navController.popBackStack()
            }
        )
    }
    bottomSheet("PaymentPasswordSettings") {
        PaymentPasswordSettings(
            onBack = {
                navController.popBackStack()
            },
            onConfirm = {
                navController.navigate("PaymentPasswordSettingsSuccess") {
                    popUpTo("PaymentPasswordSettings") {
                        inclusive = true
                    }
                }
            }
        )
    }
    dialog("PaymentPasswordSettingsSuccess") {
        MaskDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Text(text = "Payment Password changed successfully!")
            },
            text = {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_alert_change_password_description))
            },
            icon = {
                Image(
                    painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.popBackStack() },
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                }
            }
        )
    }
    bottomSheet("ChangeBackUpPassword") {
        BackupPasswordSettings(
            onBack = {
                navController.popBackStack()
            },
            onConfirm = {
                navController.navigate("ChangeBackUpPasswordSuccess") {
                    popUpTo("ChangeBackUpPassword") {
                        inclusive = true
                    }
                }
            }
        )
    }
    dialog("ChangeBackUpPasswordSuccess") {
        MaskDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_alert_change_backup_password_title))
            },
            text = {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_alert_change_backup_password_description))
            },
            icon = {
                Image(
                    painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.popBackStack() },
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                }
            }
        )
    }

    navigation("Settings_ChangeEmail_Setup", "Settings_ChangeEmail") {
        bottomSheet("Settings_ChangeEmail_Setup") {
            val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                if (it.target == RemoteBackupRecoveryViewModelBase.NavigateTarget.Code) {
                    navController.navigate("Settings_ChangeEmail_Setup_Code/${it.value.encodeUrl()}")
                }
            }
            val viewModel = getViewModel<EmailSetupViewModel> {
                parametersOf(requestNavigate)
            }
            val value by viewModel.value.observeAsState(initial = "")
            val valid by viewModel.valueValid.observeAsState(initial = true)
            val loading by viewModel.loading.observeAsState(initial = false)
            EmailInputModal(
                email = value,
                onEmailChange = { viewModel.setValue(it) },
                emailValid = valid,
                onConfirm = { viewModel.sendCode(value) },
                buttonEnabled = loading,
                title = "Set Up Email"
            )
        }
        bottomSheet(
            "Settings_ChangeEmail_Setup_Code/{email}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("email")?.let { email ->
                val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                    if (it.target == RemoteBackupRecoveryViewModelBase.NavigateTarget.Next) {
                        navController.navigate("Settings_ChangeEmail_Setup_Success") {
                            popUpTo("Settings_ChangeEmail_Setup") {
                                inclusive = true
                            }
                        }
                    }
                }
                val viewModel = getViewModel<EmailSetupViewModel> {
                    parametersOf(requestNavigate)
                }
                val code by viewModel.code.observeAsState(initial = "")
                val valid by viewModel.codeValid.observeAsState(initial = true)
                val loading by viewModel.loading.observeAsState(initial = false)
                val canSend by viewModel.canSend.observeAsState(initial = false)
                val countDown by viewModel.countdown.observeAsState(initial = 60)
                LaunchedEffect(Unit) {
                    viewModel.startCountDown()
                }
                EmailCodeInputModal(
                    email = email,
                    buttonEnabled = loading,
                    title = "Set Up Email",
                    countDown = countDown,
                    canSend = canSend,
                    codeValid = valid,
                    code = code,
                    onCodeChange = { viewModel.setCode(it) },
                    onSendCode = { viewModel.sendCode(email) },
                    onVerify = { viewModel.verifyCode(code, email) }
                )
            }
        }
        dialog("Settings_ChangeEmail_Setup_Success") {
            MaskDialog(
                onDismissRequest = { navController.popBackStack() },
                title = { Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_bind_remote_info_setup_email_title)) },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_property_1_snccess),
                        contentDescription = null
                    )
                },
                text = { Text(text = "You have successfully set up your email. ") },
                buttons = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.popBackStack() },
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                    }
                }
            )
        }
        bottomSheet(
            "Settings_ChangeEmail_Change_Code/{email}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            ),
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("email")?.let { email ->
                val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                    navController.navigate("Settings_ChangeEmail_Change_New")
                }
                val viewModel = getViewModel<EmailSetupViewModel> {
                    parametersOf(requestNavigate)
                }
                val code by viewModel.code.observeAsState(initial = "")
                val valid by viewModel.codeValid.observeAsState(initial = true)
                val loading by viewModel.loading.observeAsState(initial = false)
                val canSend by viewModel.canSend.observeAsState(initial = false)
                val countDown by viewModel.countdown.observeAsState(initial = 60)
                LaunchedEffect(Unit) {
                    viewModel.startCountDown()
                    viewModel.sendCode(email)
                }
                EmailCodeInputModal(
                    email = email,
                    buttonEnabled = loading,
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_change_email_title),
                    subTitle = { Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_change_email_tips)) },
                    countDown = countDown,
                    canSend = canSend,
                    codeValid = valid,
                    code = code,
                    onCodeChange = { viewModel.setCode(it) },
                    onSendCode = { viewModel.sendCode(email) },
                    onVerify = { viewModel.verifyCode(code, email) }
                )
            }
        }
        bottomSheet("Settings_ChangeEmail_Change_New") {
            val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                if (it.target == RemoteBackupRecoveryViewModelBase.NavigateTarget.Code) {
                    navController.navigate("Settings_ChangeEmail_Change_New_Code/${it.value.encodeUrl()}")
                }
            }
            val viewModel = getViewModel<EmailSetupViewModel> {
                parametersOf(requestNavigate)
            }
            val value by viewModel.value.observeAsState(initial = "")
            val valid by viewModel.valueValid.observeAsState(initial = true)
            val loading by viewModel.loading.observeAsState(initial = false)
            EmailInputModal(
                email = value,
                onEmailChange = { viewModel.setValue(it) },
                emailValid = valid,
                onConfirm = { viewModel.sendCode(value) },
                buttonEnabled = loading,
                title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_change_email_title)
            )
        }

        bottomSheet(
            "Settings_ChangeEmail_Change_New_Code/{email}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("email")?.let { email ->
                val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                    if (it.target == RemoteBackupRecoveryViewModelBase.NavigateTarget.Next) {
                        navController.navigate("Settings_ChangeEmail_Change_Success") {
                            popUpTo("Settings_ChangeEmail_Change_Code") {
                                inclusive = true
                            }
                        }
                    }
                }
                val viewModel = getViewModel<EmailSetupViewModel> {
                    parametersOf(requestNavigate)
                }
                val code by viewModel.code.observeAsState(initial = "")
                val valid by viewModel.codeValid.observeAsState(initial = true)
                val loading by viewModel.loading.observeAsState(initial = false)
                val canSend by viewModel.canSend.observeAsState(initial = false)
                val countDown by viewModel.countdown.observeAsState(initial = 60)
                LaunchedEffect(Unit) {
                    viewModel.startCountDown()
                }
                EmailCodeInputModal(
                    email = email,
                    buttonEnabled = loading,
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_change_email_title),
                    countDown = countDown,
                    canSend = canSend,
                    codeValid = valid,
                    code = code,
                    onCodeChange = { viewModel.setCode(it) },
                    onSendCode = { viewModel.sendCode(email) },
                    onVerify = { viewModel.verifyCode(code, email) }
                )
            }
        }
        dialog("Settings_ChangeEmail_Change_Success") {
            MaskDialog(
                onDismissRequest = { navController.popBackStack() },
                title = { Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_bind_remote_info_change_email_title)) },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_property_1_snccess),
                        contentDescription = null
                    )
                },
                text = { Text(text = "You have successfully changed your email. ") },
                buttons = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.popBackStack() },
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                    }
                }
            )
        }

    }

    navigation("Settings_ChangePhone_Setup", "Settings_ChangePhone") {
        bottomSheet("Settings_ChangePhone_Setup") {
            val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                if (it.target == RemoteBackupRecoveryViewModelBase.NavigateTarget.Code) {
                    navController.navigate("Settings_ChangePhone_Setup_Code/${it.value.encodeUrl()}")
                }
            }
            val viewModel = getViewModel<PhoneSetupViewModel> {
                parametersOf(requestNavigate)
            }
            val regionCode by viewModel.regionCode.observeAsState(initial = "+86")
            val phone by viewModel.value.observeAsState(initial = "")
            val valid by viewModel.valueValid.observeAsState(initial = true)
            val loading by viewModel.loading.observeAsState(initial = true)
            PhoneInputModal(
                regionCode = regionCode,
                onRegionCodeChange = { viewModel.setRegionCode(it) },
                phone = phone,
                onPhoneChange = { viewModel.setValue(it) },
                phoneValid = valid,
                onConfirm = { viewModel.sendCode(regionCode + phone) },
                buttonEnabled = loading,
                title = "Set Up Phone Number",
            )
        }
        bottomSheet(
            "Settings_ChangePhone_Setup_Code/{phone}",
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("phone")?.let { phone ->
                val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                    if (it.target == RemoteBackupRecoveryViewModelBase.NavigateTarget.Next) {
                        navController.navigate("Settings_ChangePhone_Setup_Success") {
                            popUpTo("Settings_ChangePhone_Setup") {
                                inclusive = true
                            }
                        }
                    }
                }
                val viewModel = getViewModel<PhoneSetupViewModel> {
                    parametersOf(requestNavigate)
                }
                val code by viewModel.code.observeAsState(initial = "")
                val canSend by viewModel.canSend.observeAsState(initial = false)
                val valid by viewModel.codeValid.observeAsState(initial = true)
                val countDown by viewModel.countdown.observeAsState(initial = 60)
                val loading by viewModel.loading.observeAsState(initial = false)
                PhoneCodeInputModal(
                    phone = phone,
                    code = code,
                    onCodeChange = { viewModel.setCode(it) },
                    canSend = canSend,
                    codeValid = valid,
                    countDown = countDown,
                    buttonEnabled = loading,
                    onSendCode = { viewModel.sendCode(phone) },
                    onVerify = { viewModel.verifyCode(code = code, value = phone) },
                    title = "Set Up Phone Number"
                )
            }
        }
        dialog("Settings_ChangePhone_Setup_Success") {
            MaskDialog(
                onDismissRequest = { navController.popBackStack() },
                title = { Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_bind_remote_info_setup_phone_number_title)) },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_property_1_snccess),
                        contentDescription = null
                    )
                },
                text = { Text(text = "You have successfully set up your email. ") },
                buttons = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.popBackStack() },
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                    }
                }
            )
        }


        bottomSheet(
            "Settings_ChangePhone_Change_Code/{phone}",
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("phone")?.let { phone ->
                val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                    if (it.target == RemoteBackupRecoveryViewModelBase.NavigateTarget.Next) {
                        navController.navigate("Settings_ChangePhone_Change_New")
                    }
                }
                val viewModel = getViewModel<PhoneSetupViewModel> {
                    parametersOf(requestNavigate)
                }
                val code by viewModel.code.observeAsState(initial = "")
                val canSend by viewModel.canSend.observeAsState(initial = false)
                val valid by viewModel.codeValid.observeAsState(initial = true)
                val countDown by viewModel.countdown.observeAsState(initial = 60)
                val loading by viewModel.loading.observeAsState(initial = false)
                PhoneCodeInputModal(
                    phone = phone,
                    code = code,
                    onCodeChange = { viewModel.setCode(it) },
                    canSend = canSend,
                    codeValid = valid,
                    countDown = countDown,
                    buttonEnabled = loading,
                    onSendCode = { viewModel.sendCode(phone) },
                    onVerify = { viewModel.verifyCode(code = code, value = phone) },
                    title = "Change Phone Number",
                    subTitle = { Text(text = "To change your phone, you need to verify your current phone number.") }
                )
            }
        }
        bottomSheet("Settings_ChangePhone_Change_New") {
            val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                if (it.target == RemoteBackupRecoveryViewModelBase.NavigateTarget.Code) {
                    navController.navigate("Settings_ChangePhone_Change_New_Code/${it.value.encodeUrl()}")
                }
            }
            val viewModel = getViewModel<PhoneSetupViewModel> {
                parametersOf(requestNavigate)
            }
            val regionCode by viewModel.regionCode.observeAsState(initial = "+86")
            val phone by viewModel.value.observeAsState(initial = "")
            val valid by viewModel.valueValid.observeAsState(initial = true)
            val loading by viewModel.loading.observeAsState(initial = true)
            PhoneInputModal(
                regionCode = regionCode,
                onRegionCodeChange = { viewModel.setRegionCode(it) },
                phone = phone,
                onPhoneChange = { viewModel.setValue(it) },
                phoneValid = valid,
                onConfirm = { viewModel.sendCode(regionCode + phone) },
                buttonEnabled = loading,
                title = "Change Phone Number",
            )
        }

        bottomSheet(
            "Settings_ChangePhone_Change_New_Code/{phone}",
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("phone")?.let { phone ->
                val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                    if (it.target == RemoteBackupRecoveryViewModelBase.NavigateTarget.Next) {
                        navController.navigate("Settings_ChangePhone_Change_Success") {
                            popUpTo("Settings_ChangePhone_Change_Code") {
                                inclusive = true
                            }
                        }
                    }
                }
                val viewModel = getViewModel<PhoneSetupViewModel> {
                    parametersOf(requestNavigate)
                }
                val code by viewModel.code.observeAsState(initial = "")
                val canSend by viewModel.canSend.observeAsState(initial = false)
                val valid by viewModel.codeValid.observeAsState(initial = true)
                val countDown by viewModel.countdown.observeAsState(initial = 60)
                val loading by viewModel.loading.observeAsState(initial = false)
                PhoneCodeInputModal(
                    phone = phone,
                    code = code,
                    onCodeChange = { viewModel.setCode(it) },
                    canSend = canSend,
                    codeValid = valid,
                    countDown = countDown,
                    buttonEnabled = loading,
                    onSendCode = { viewModel.sendCode(phone) },
                    onVerify = { viewModel.verifyCode(code = code, value = phone) },
                    title = "Change Phone Number"
                )
            }
        }
        dialog("Settings_ChangePhone_Change_Success") {
            MaskDialog(
                onDismissRequest = { navController.popBackStack() },
                title = { Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_bind_remote_info_change_phone_number_title)) },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_property_1_snccess),
                        contentDescription = null
                    )
                },
                text = { Text(text = "You have successfully changed your phone number.") },
                buttons = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.popBackStack() },
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                    }
                }
            )
        }

    }

    backup(navController = navController)
}
