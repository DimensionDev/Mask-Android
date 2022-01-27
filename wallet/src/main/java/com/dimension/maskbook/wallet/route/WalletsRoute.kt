package com.dimension.maskbook.wallet.route

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.*
import androidx.navigation.compose.dialog
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.copyText
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ext.shareText
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.ITokenRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.ui.scenes.wallets.UnlockWalletDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.WalletQrcodeScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.collectible.CollectibleDetailScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.MultiChainWalletDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.CreateOrImportWalletScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.CreateType
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.create.CreateWalletHost
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.import.ImportWalletHost
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.LegalScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password.BiometricsEnableScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password.SetUpPaymentPassword
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password.TouchIdEnableScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.*
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.SendTokenHost
import com.dimension.maskbook.wallet.ui.scenes.wallets.token.TokenDetailScene
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.viewmodel.wallets.*
import com.dimension.maskbook.wallet.viewmodel.wallets.collectible.CollectibleDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.*
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf


@ExperimentalAnimationApi
@ExperimentalMaterialNavigationApi
fun NavGraphBuilder.walletsRoute(
    navController: NavController
) {
    composable(
        "CollectibleDetail/{id}",
        arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )
    ) {
        it.arguments?.getString("id")?.let { id ->
            val viewModel = getViewModel<CollectibleDetailViewModel> {
                parametersOf(id)
            }
            val data by viewModel.data.observeAsState(initial = null)
            data?.let {
                CollectibleDetailScene(
                    data = it,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSend = {

                    },
                    onReceive = {
                        navController.navigate("WalletQrcode/${it.chainType.name}")
                    }
                )
            }
        }
    }

    composable(
    "WalletQrcode/{name}",
        arguments = listOf(navArgument("name"){ type = NavType.StringType })
    ) {
        val repository = get<IWalletRepository>()
        val currentWallet by repository.currentWallet.observeAsState(initial = null)
        val name = it.arguments?.getString("name") ?: ""
        val context = LocalContext.current
        currentWallet?.let {
            WalletQrcodeScene(
                address = it.address,
                name = name,
                onShare = { context.shareText(it.address) },
                onBack = { navController.popBackStack() },
                onCopy = { context.copyText(it.address) }
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
            val dWebData by viewModel.dWebData.observeAsState(initial = null)
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
                            if (token.chainType != dWebData?.chainType) {
                                navController.navigate("WalletNetworkSwitch/${token.chainType}")
                            } else {
                                navController.navigate("SendTokenScene/${token.address}")
                            }
                        },
                        onReceive = {
                            navController.navigate("WalletQrcode/${token.symbol}")
                        },
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

    dialog(
        "WalletNetworkSwitch/{target}",
        arguments = listOf(navArgument("target") { type = NavType.StringType })
    ) {
        it.arguments?.getString("target")?.let {
            ChainType.valueOf(it)
        }?.let { target ->
            val viewModel = getViewModel<WalletSwitchViewModel>()
            val currentNetwork by viewModel.network.observeAsState(initial = ChainType.eth)
            WalletNetworkSwitchWarningDialog(
                currentNetwork = currentNetwork.name,
                connectingNetwork = target.name,
                onCancel = { navController.popBackStack() },
                onSwitch = {
                    viewModel.setChainType(target)
                    navController.popBackStack()
                }
            )
        }
    }

    dialog("WalletNetworkSwitchWarningDialog") {
        val viewModel = getViewModel<WalletSwitchViewModel>()
        val currentNetwork by viewModel.network.observeAsState(initial = ChainType.eth)
        val wallet by viewModel.currentWallet.observeAsState(initial = null)
        wallet?.let {
            if (!it.fromWalletConnect || it.walletConnectChainType == currentNetwork || it.walletConnectChainType == null) navController.popBackStack()
            WalletNetworkSwitchWarningDialog(
                currentNetwork = currentNetwork.name,
                connectingNetwork = it.walletConnectChainType?.name ?: "",
                onCancel = { navController.popBackStack() },
                onSwitch = {
                    it.walletConnectChainType?.let { type ->
                        viewModel.setChainType(type)
                    }
                    navController.popBackStack()
                }
            )
        }
    }

    bottomSheet("SwitchWallet") {
        val viewModel = getViewModel<WalletSwitchViewModel>()
        val wallet by viewModel.currentWallet.observeAsState(initial = null)
        val wallets by viewModel.wallets.observeAsState(initial = emptyList())
        val chainType by viewModel.network.observeAsState(initial = ChainType.eth)
        wallet?.let { it1 ->
            WalletSwitchSceneModal(
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
                    navController.navigate("WalletSwitchEditModal/${it.id}")
                }
            )
        }
    }
    bottomSheet(
        "WalletSwitchEditModal/{id}",
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) {
        it.arguments?.getString("id")?.let { id ->
            val repository = get<IWalletRepository>()
            val wallets by repository.wallets.observeAsState(initial = emptyList())
            val viewModel = getViewModel<WalletConnectManagementViewModel>()
            wallets.firstOrNull { it.id == id }?.let { wallet ->
                WalletSwitchEditModal(
                    walletData = wallet,
                    onRename = { navController.navigate("WalletManagementRename/${wallet.id}") },
                    onDelete = {
                        navController.popBackStack()
                        navController.navigate("WalletManagementDeleteDialog/${wallet.id}")
                    },
                    onDisconnect = {
                        viewModel.disconnect(walletData = wallet)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
    bottomSheet("WalletBalancesMenu") {
        val viewModel = getViewModel<WalletManagementModalViewModel>()
        val currentWallet by viewModel.currentWallet.observeAsState(initial = null)
        val wcViewModel = getViewModel<WalletConnectManagementViewModel>()
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
                    wcViewModel.disconnect(walletData = wallet)
                    navController.popBackStack()
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
                Text(text = stringResource(R.string.common_alert_biometry_id_activate_title))
            },
            text = {
                Text(text = stringResource(R.string.common_alert_biometry_id_activate_description))
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
                    Text(text = stringResource(R.string.common_controls_done))
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
                Text(text = stringResource(R.string.common_alert_biometry_id_activate_title))
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
                    Text(text = stringResource(R.string.common_controls_done))
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
                    navController.navigate(
                        Uri.parse("maskwallet://Home/Wallets"),
                        navOptions = navOptions {
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
                    navController.navigate(
                        Uri.parse("maskwallet://Home/Wallets"),
                        navOptions = navOptions {
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
                        if (passwordValid) {
                            navController.navigate(it, navOptions {
                                popUpTo("UnlockWalletDialog") {
                                    inclusive = true
                                }
                            })
                        }
                    } ?: navController.popBackStack()
                }
            }
        )
    }
}
