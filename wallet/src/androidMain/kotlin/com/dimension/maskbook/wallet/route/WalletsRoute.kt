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
package com.dimension.maskbook.wallet.route

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ext.shareText
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.ui.notification.StringResNotificationEvent.Companion.show
import com.dimension.maskbook.common.ui.widget.LocalInAppNotification
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.common.viewmodel.BiometricEnableViewModel
import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.ChainType
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
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.BackupWalletScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletConnectModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletDeleteDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletManagementModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletNetworkSwitchWarningDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletRenameModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletSwitchAddModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletSwitchEditModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletSwitchSceneModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletTransactionHistoryScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.SendTokenHost
import com.dimension.maskbook.wallet.ui.scenes.wallets.token.TokenDetailScene
import com.dimension.maskbook.wallet.viewmodel.wallets.BiometricViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.TokenDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.UnlockWalletViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletConnectManagementViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletManagementModalViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.collectible.CollectibleDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletDeleteViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletRenameViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletSwitchViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletTransactionHistoryViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
fun NavGraphBuilder.walletsRoute(
    navController: NavController
) {
    composable(
        WalletRoute.CollectibleDetail.path,
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
                        navController.navigate(WalletRoute.WalletQrcode(it.chainType.name))
                    }
                )
            }
        }
    }

    composable(
        WalletRoute.WalletQrcode.path,
        arguments = listOf(navArgument("name") { type = NavType.StringType })
    ) {
        val repository = get<IWalletRepository>()
        val currentWallet by repository.currentWallet.observeAsState(initial = null)
        val name = it.arguments?.getString("name") ?: ""
        val context = LocalContext.current
        val clipboardManager = LocalClipboardManager.current
        val inAppNotification = LocalInAppNotification.current
        currentWallet?.let {
            WalletQrcodeScene(
                address = it.address,
                name = name,
                onShare = { context.shareText(it.address) },
                onBack = { navController.popBackStack() },
                onCopy = {
                    clipboardManager.setText(buildAnnotatedString { append(it.address) })
                    inAppNotification.show(R.string.common_alert_copied_to_clipboard_title)
                }
            )
        }
    }
    composable(
        WalletRoute.TokenDetail.path,
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
                                navController.navigate(WalletRoute.WalletNetworkSwitch(token.chainType.name))
                            } else {
                                navController.navigate(WalletRoute.SendTokenScene(token.address))
                            }
                        },
                        onReceive = {
                            navController.navigate(WalletRoute.WalletQrcode(token.symbol))
                        },
                    )
                }
            }
        }
    }
    bottomSheet(WalletRoute.SwitchWalletAdd) {
        WalletSwitchAddModal(
            onCreate = {
                navController.navigate(WalletRoute.WalletIntroHostLegal(CreateType.CREATE.name))
            },
            onImport = {
                navController.navigate(WalletRoute.WalletIntroHostLegal(CreateType.IMPORT.name))
            },
        )
    }
    bottomSheet(WalletRoute.SwitchWalletAddWalletConnect) {
        WalletConnectModal()
    }

    dialog(
        WalletRoute.WalletNetworkSwitch.path,
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

    dialog(WalletRoute.WalletNetworkSwitchWarningDialog) {
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

    bottomSheet(WalletRoute.SwitchWallet) {
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
                    navController.navigate(WalletRoute.SwitchWalletAdd) {
                        popUpTo(WalletRoute.SwitchWallet) {
                            inclusive = true
                        }
                    }
                },
                onWalletConnectClicked = {
                    navController.navigate(WalletRoute.SwitchWalletAddWalletConnect) {
                        popUpTo(WalletRoute.SwitchWallet) {
                            inclusive = true
                        }
                    }
                },
                onEditMenuClicked = { wallet ->
                    navController.navigate(WalletRoute.WalletSwitchEditModal(wallet.id)) {
                        popUpTo(WalletRoute.SwitchWallet) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
    bottomSheet(
        WalletRoute.WalletSwitchEditModal.path,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) {
        it.arguments?.getString("id")?.let { id ->
            val repository = get<IWalletRepository>()
            val wallets by repository.wallets.observeAsState(initial = emptyList())
            val viewModel = getViewModel<WalletConnectManagementViewModel>()
            wallets.firstOrNull { it.id == id }?.let { wallet ->
                WalletSwitchEditModal(
                    walletData = wallet,
                    onRename = { navController.navigate(WalletRoute.WalletManagementRename(wallet.id, wallet.name)) },
                    onDelete = {
                        navController.popBackStack()
                        navController.navigate(WalletRoute.WalletManagementDeleteDialog(wallet.id))
                    },
                    onDisconnect = {
                        viewModel.disconnect(walletData = wallet)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
    bottomSheet(WalletRoute.WalletBalancesMenu) {
        val viewModel = getViewModel<WalletManagementModalViewModel>()
        val currentWallet by viewModel.currentWallet.observeAsState(initial = null)
        val wcViewModel = getViewModel<WalletConnectManagementViewModel>()
        currentWallet?.let { wallet ->
            WalletManagementModal(
                walletData = wallet,
                onRename = { navController.navigate(WalletRoute.WalletManagementRename(wallet.id, wallet.name)) },
                onBackup = { navController.navigate(WalletRoute.UnlockWalletDialog(WalletRoute.WalletManagementBackup)) },
                onTransactionHistory = { navController.navigate(WalletRoute.WalletManagementTransactionHistory) },
                onDelete = {
                    navController.popBackStack()
                    navController.navigate(WalletRoute.WalletManagementDeleteDialog(wallet.id))
                },
                onDisconnect = {
                    wcViewModel.disconnect(walletData = wallet)
                    navController.popBackStack()
                }
            )
        }
    }
    dialog(
        WalletRoute.WalletManagementDeleteDialog.path,
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
    composable(WalletRoute.WalletManagementBackup) {
        val viewModel = getViewModel<WalletBackupViewModel>()
        val keyStore by viewModel.keyStore.observeAsState(initial = "")
        val privateKey by viewModel.privateKey.observeAsState(initial = "")
        BackupWalletScene(
            keyStore = keyStore,
            privateKey = privateKey,
            onBack = { navController.popBackStack() },
        )
    }
    composable(WalletRoute.WalletManagementTransactionHistory) {
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
        WalletRoute.WalletManagementRename.path,
        arguments = listOf(
            navArgument("id") { type = NavType.StringType },
            navArgument("name") { type = NavType.StringType }
        )
    ) {
        val walletId = it.arguments?.getString("id") ?: return@bottomSheet
        val walletName = it.arguments?.getString("name").orEmpty()

        val viewModel = getViewModel<WalletRenameViewModel> {
            parametersOf(walletId, walletName)
        }
        val name by viewModel.name.observeAsState()
        WalletRenameModal(
            name = name,
            onNameChanged = { viewModel.setName(it) },
            onDone = {
                viewModel.confirm()
                navController.navigate(
                    Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Wallet)),
                    navOptions {
                        launchSingleTop = true
                        popUpTo(CommonRoute.Main.Home) {
                            inclusive = false
                        }
                    }
                )
            },
        )
    }
    composable(
        WalletRoute.WalletIntroHostLegal.path,
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        val repo = get<SettingServices>()
        val password by repo.paymentPassword.observeAsState(initial = null)
        val enableBiometric by repo.biometricEnabled.observeAsState(initial = false)
        val shouldShowLegalScene by repo.shouldShowLegalScene.observeAsState(initial = true)
        val biometricEnableViewModel: BiometricEnableViewModel = getViewModel()
        val context = LocalContext.current
        val next: () -> Unit = {
            val route = if (password.isNullOrEmpty()) {
                WalletRoute.WalletIntroHostPassword(type.name)
            } else if (!enableBiometric && biometricEnableViewModel.isSupported(context)) {
                WalletRoute.WalletIntroHostFaceId(type.name)
            } else {
                WalletRoute.CreateOrImportWallet(type.name)
            }
            navController.navigate(
                route,
                navOptions {
                    popUpTo(id = it.destination.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            )
        }
        if (!shouldShowLegalScene) {
            next()
        }
        LegalScene(
            onBack = { navController.popBackStack() },
            onAccept = {
                repo.setShouldShowLegalScene(false)
            },
            onBrowseAgreement = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://legal.mask.io/maskbook/privacy-policy-ios.html")
                    )
                )
            }
        )
    }

    bottomSheet(
        WalletRoute.WalletIntroHostPassword.path,
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        val enableBiometric by get<SettingServices>().biometricEnabled.observeAsState(initial = false)
        val biometricEnableViewModel: BiometricEnableViewModel = getViewModel()
        val context = LocalContext.current
        SetUpPaymentPassword(
            onNext = {
                if (!enableBiometric && biometricEnableViewModel.isSupported(context)) {
                    navController.navigate(WalletRoute.WalletIntroHostFaceId(type.name))
                } else {
                    navController.navigate(WalletRoute.CreateOrImportWallet(type.name))
                }
            }
        )
    }

    composable(
        WalletRoute.WalletIntroHostFaceId.path,
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
                    navController.navigate(WalletRoute.WalletIntroHostFaceIdEnableSuccess(type.name))
                } else {
                    navController.navigate(WalletRoute.CreateOrImportWallet(type.name))
                }
            }
        )
    }

    dialog(
        WalletRoute.WalletIntroHostFaceIdEnableSuccess.path,
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        MaskDialog(
            onDismissRequest = {
                navController.navigate(WalletRoute.CreateOrImportWallet(type.name))
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
                        navController.navigate(WalletRoute.CreateOrImportWallet(type.name))
                    },
                ) {
                    Text(text = stringResource(R.string.common_controls_done))
                }
            }
        )
    }

    composable(
        WalletRoute.WalletIntroHostTouchId.path,
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
                navController.navigate(WalletRoute.WalletIntroHostTouchIdEnableSuccess(type.name))
            }
        )
    }

    dialog(
        WalletRoute.WalletIntroHostTouchIdEnableSuccess.path,
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
        )
    ) {
        val type = it.arguments?.getString("type")?.let { type ->
            CreateType.valueOf(type)
        } ?: CreateType.CREATE
        MaskDialog(
            onDismissRequest = {
                navController.navigate(WalletRoute.CreateOrImportWallet(type.name))
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
                        navController.navigate(WalletRoute.CreateOrImportWallet(type.name))
                    },
                ) {
                    Text(text = stringResource(R.string.common_controls_done))
                }
            }
        )
    }

    composable(
        WalletRoute.CreateOrImportWallet.path,
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

    dialog(WalletRoute.MultiChainWalletDialog) {
        MultiChainWalletDialog()
    }

    composable(
        WalletRoute.CreateWallet.path,
        arguments = listOf(
            navArgument("wallet") { type = NavType.StringType },
        )
    ) {
        it.arguments?.getString("wallet")?.let { wallet ->
            CreateWalletHost(
                wallet = wallet,
                onDone = {
                    navController.navigate(
                        Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Wallet)),
                        navOptions = navOptions {
                            launchSingleTop = true
                            popUpTo(CommonRoute.Main.Home) {
                                inclusive = false
                            }
                        }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }
    }

    composable(
        WalletRoute.ImportWallet.path,
        arguments = listOf(
            navArgument("wallet") { type = NavType.StringType },
        )
    ) {
        it.arguments?.getString("wallet")?.let { wallet ->
            ImportWalletHost(
                wallet = wallet,
                onDone = {
                    navController.navigate(
                        Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Wallet)),
                        navOptions = navOptions {
                            launchSingleTop = true
                            popUpTo(CommonRoute.Main.Home) {
                                inclusive = false
                            }
                        }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }
    }

    composable(
        WalletRoute.SendTokenScene.path,
        arguments = listOf(
            navArgument("tokenAddress") { type = NavType.StringType }
        )
    ) {
        SendTokenHost(
            tokenAddress = it.arguments?.getString("tokenAddress").orEmpty(),
            onBack = {
                navController.popBackStack()
            },
            onDone = {
                navController.popBackStack()
            }
        )
    }

    dialog(
        WalletRoute.UnlockWalletDialog.path,
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
                                navController.navigate(
                                    it,
                                    navOptions {
                                        popUpTo(WalletRoute.UnlockWalletDialog.path) {
                                            inclusive = true
                                        }
                                    }
                                )
                            } ?: navController.popBackStack()
                        }
                    )
                } else {
                    target?.let {
                        if (passwordValid) {
                            navController.navigate(
                                it,
                                navOptions {
                                    popUpTo(WalletRoute.UnlockWalletDialog.path) {
                                        inclusive = true
                                    }
                                }
                            )
                        }
                    } ?: navController.popBackStack()
                }
            }
        )
    }
}
