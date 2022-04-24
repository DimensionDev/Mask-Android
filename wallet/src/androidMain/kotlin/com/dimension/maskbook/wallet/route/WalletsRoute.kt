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

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ext.openUrl
import com.dimension.maskbook.common.ext.shareText
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.notification.StringResNotificationEvent.Companion.show
import com.dimension.maskbook.common.ui.scene.SetUpPaymentPassword
import com.dimension.maskbook.common.ui.widget.LocalInAppNotification
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.viewmodel.BiometricEnableViewModel
import com.dimension.maskbook.common.viewmodel.BiometricViewModel
import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.WalletData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.ui.scenes.wallets.UnlockWalletDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.WalletQrcodeScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.collectible.CollectibleDetailScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.MultiChainWalletDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.CreateOrImportWalletScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.create.CreateType
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.LegalScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password.BiometricsEnableScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password.TouchIdEnableScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.BackupWalletScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletDeleteDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletManagementModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletNetworkSwitchWarningDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletRenameModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletSwitchAddModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletSwitchEditModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletSwitchSceneModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.WalletTransactionHistoryScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.EmptyTokenDialog
import com.dimension.maskbook.wallet.ui.scenes.wallets.token.TokenDetailScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.walletconnect.DAppConnectedModal
import com.dimension.maskbook.wallet.ui.scenes.wallets.walletconnect.WalletConnectModal
import com.dimension.maskbook.wallet.viewmodel.wallets.TokenDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.UnlockWalletViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletConnectManagementViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletManagementModalViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.collectible.CollectibleDetailViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletDeleteViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletRenameViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletSwitchEditViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletSwitchViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.management.WalletTransactionHistoryViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.walletconnect.DAppConnectedViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
    route = WalletRoute.CollectibleDetail.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun CollectibleDetail(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("id") id: String,
) {
    val viewModel = getViewModel<CollectibleDetailViewModel> {
        parametersOf(id)
    }
    val context = LocalContext.current
    val data by viewModel.data.observeAsState(initial = null)
    val transactions by viewModel.transactions.observeAsState()
    val nativeToken by viewModel.walletNativeToken.observeAsState()
    CollectibleDetailScene(
        data = data,
        onBack = onBack,
        onSend = {
            data?.let { collectible ->
                nativeToken?.let {
                    navController.transfer(walletNativeToken = it, tradableId = collectible.tradableId())
                }
            }
        },
        onOpenSeaClicked = {
            data?.let {
                context.openUrl(it.link)
            }
        },
        transactions = transactions,
        onSpeedUp = {},
        onCancel = {}
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletQrcode.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun WalletQrcode(
    @Back onBack: () -> Unit,
    @Path("name") name: String,
) {
    val repository = get<IWalletRepository>()
    val currentWallet by repository.currentWallet.observeAsState(initial = null)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val inAppNotification = LocalInAppNotification.current
    WalletQrcodeScene(
        address = currentWallet?.address.orEmpty(),
        name = name,
        onShare = { context.shareText(currentWallet?.address.orEmpty()) },
        onBack = onBack,
        onCopy = {
            clipboardManager.setText(buildAnnotatedString { append(currentWallet?.address.orEmpty()) })
            inAppNotification.show(R.string.common_alert_copied_to_clipboard_title)
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.TokenDetail.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun TokenDetail(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("id") id: String,
) {
    val viewModel = getViewModel<TokenDetailViewModel> {
        parametersOf(id)
    }
    val token by viewModel.tokenData.observeAsState()
    val transactions by viewModel.transactions.observeAsState()
    val walletTokenData by viewModel.walletTokenData.observeAsState()
    val dWebData by viewModel.dWebData.observeAsState()
    val nativeToken by viewModel.walletNativeToken.observeAsState()

    TokenDetailScene(
        onBack = onBack,
        tokenData = token,
        walletTokenData = walletTokenData,
        transactions = transactions,
        onSpeedUp = { },
        onCancel = { },
        onSend = {
            token?.let { token ->
                if (token.chainType != dWebData?.chainType) {
                    navController.navigate(WalletRoute.WalletNetworkSwitch(token.chainType.name))
                } else {
                    nativeToken?.let {
                        navController.transfer(walletNativeToken = it, tradableId = token.address)
                    }
                }
            }
        },
        onReceive = {
            token?.let { token ->
                navController.navigate(WalletRoute.WalletQrcode(token.symbol))
            }
        },
    )
}

@NavGraphDestination(
    route = WalletRoute.SwitchWalletAdd,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun SwitchWalletAdd(
    navController: NavController,
) {
    val context = LocalContext.current
    val settingService = get<SettingServices>()
    val password by settingService.paymentPassword.collectAsState("")
    val enableBiometric by settingService.biometricEnabled.collectAsState(false)
    val biometricEnableViewModel = getViewModel<BiometricEnableViewModel>()
    val shouldShowLegalScene by settingService.shouldShowLegalScene.collectAsState(false)
    val wallets by get<IWalletRepository>().wallets.collectAsState(emptyList())
    WalletSwitchAddModal(
        onCreate = {
            navigateWalletCreate(
                type = CreateType.CREATE,
                password = password,
                enableBiometric = enableBiometric,
                biometricEnableViewModel = biometricEnableViewModel,
                context = context,
                wallets = wallets,
                shouldShowLegalScene = shouldShowLegalScene,
                navController = navController,
            )
        },
        onImport = {
            navigateWalletCreate(
                type = CreateType.IMPORT,
                password = password,
                enableBiometric = enableBiometric,
                biometricEnableViewModel = biometricEnableViewModel,
                context = context,
                wallets = wallets,
                shouldShowLegalScene = shouldShowLegalScene,
                navController = navController,
            )
        },
    )
}

@NavGraphDestination(
    route = WalletRoute.SwitchWalletAddWalletConnect,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun SwitchWalletAddWalletConnect(
    navController: NavController,
) {
    WalletConnectModal(
        rootNavController = navController,
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletNetworkSwitch.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun WalletNetworkSwitch(
    @Back onBack: () -> Unit,
    @Path("target") targetString: String,
) {
    val target = remember(targetString) { ChainType.valueOf(targetString) }
    val viewModel = getViewModel<WalletSwitchViewModel>()
    val currentNetwork by viewModel.network.observeAsState(initial = ChainType.eth)
    WalletNetworkSwitchWarningDialog(
        currentNetwork = currentNetwork.name,
        connectingNetwork = target.name,
        onCancel = onBack,
        onSwitch = {
            viewModel.setChainType(target)
            onBack.invoke()
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletNetworkSwitchWarningDialog,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun WalletNetworkSwitchWarningDialog(
    @Back onBack: () -> Unit,
) {
    val viewModel = getViewModel<WalletSwitchViewModel>()
    val currentNetwork by viewModel.network.observeAsState(initial = ChainType.eth)
    val wallet by viewModel.currentWallet.observeAsState(initial = null)
    LaunchedEffect(wallet) {
        wallet?.let { wallet ->
            if (!wallet.fromWalletConnect || wallet.walletConnectChainType == currentNetwork || wallet.walletConnectChainType == null) {
                onBack.invoke()
            }
        }
    }
    WalletNetworkSwitchWarningDialog(
        currentNetwork = currentNetwork.name,
        connectingNetwork = wallet?.walletConnectChainType?.name.orEmpty(),
        onCancel = onBack,
        onSwitch = {
            wallet?.walletConnectChainType?.let { type ->
                viewModel.setChainType(type)
            }
            onBack.invoke()
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.SwitchWallet,
    deeplink = [
        Deeplinks.Wallet.SwitchWallet,
    ],
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun SwitchWallet(
    navController: NavController,
) {
    val viewModel = getViewModel<WalletSwitchViewModel>()
    val wallet by viewModel.currentWallet.observeAsState(initial = null)
    val wallets by viewModel.wallets.observeAsState(initial = emptyList())
    val chainType by viewModel.network.observeAsState(initial = ChainType.eth)
    WalletSwitchSceneModal(
        selectedWallet = wallet,
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
        onEditMenuClicked = {
            navController.navigate(WalletRoute.WalletSwitchEditModal(it.id)) {
                popUpTo(WalletRoute.SwitchWallet) {
                    inclusive = true
                }
            }
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletSwitchEditModal.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun WalletSwitchEditModal(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("id") id: String,
) {
    val viewModel = getViewModel<WalletConnectManagementViewModel>()
    val editViewModel = getViewModel<WalletSwitchEditViewModel> {
        parametersOf(id)
    }
    val wallet by editViewModel.wallet.collectAsState(initial = null)
    WalletSwitchEditModal(
        walletData = wallet,
        onRename = {
            wallet?.let { wallet ->
                navController.navigate(
                    WalletRoute.WalletManagementRename(
                        wallet.id,
                        wallet.name
                    )
                )
            }
        },
        onDelete = {
            onBack.invoke()
            wallet?.let { wallet ->
                navController.navigate(WalletRoute.WalletManagementDeleteDialog(wallet.id))
            }
        },
        onDisconnect = {
            wallet?.let { viewModel.disconnect(walletData = it) }
            onBack.invoke()
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletBalancesMenu,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun WalletBalancesMenu(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val viewModel = getViewModel<WalletManagementModalViewModel>()
    val currentWallet by viewModel.currentWallet.observeAsState(initial = null)
    val wcViewModel = getViewModel<WalletConnectManagementViewModel>()
    WalletManagementModal(
        walletData = currentWallet,
        onRename = {
            currentWallet?.let { wallet ->
                navController.navigate(WalletRoute.WalletManagementRename(wallet.id, wallet.name))
            }
        },
        onBackup = {
            navController.navigate(WalletRoute.UnlockWalletDialog(WalletRoute.WalletManagementBackup))
        },
        onTransactionHistory = {
            navController.navigate(WalletRoute.WalletManagementTransactionHistory)
        },
        onDelete = {
            onBack.invoke()
            currentWallet?.let { wallet ->
                navController.navigate(WalletRoute.WalletManagementDeleteDialog(wallet.id))
            }
        },
        onDisconnect = {
            currentWallet?.let { wallet ->
                wcViewModel.disconnect(walletData = wallet)
            }
            onBack.invoke()
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletManagementDeleteDialog.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun WalletManagementDeleteDialog(
    @Back onBack: () -> Unit,
    @Path("id") id: String,
) {
    val viewModel = getViewModel<WalletDeleteViewModel> {
        parametersOf(id)
    }
    val biometricViewModel = getViewModel<BiometricViewModel>()
    val wallet by viewModel.wallet.observeAsState(initial = null)
    val biometricEnabled by biometricViewModel.biometricEnabled.observeAsState(initial = false)
    val context = LocalContext.current
    val password by viewModel.password.observeAsState(initial = "")
    val canConfirm by viewModel.canConfirm.observeAsState(initial = false)
    WalletDeleteDialog(
        walletData = wallet,
        password = password,
        onPasswordChanged = { viewModel.setPassword(it) },
        onBack = onBack,
        onDelete = {
            if (biometricEnabled) {
                biometricViewModel.authenticate(
                    context = context,
                    onSuccess = {
                        viewModel.confirm()
                        onBack.invoke()
                    }
                )
            } else {
                viewModel.confirm()
                onBack.invoke()
            }
        },
        passwordValid = canConfirm,
        biometricEnabled = biometricEnabled
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletManagementBackup,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun WalletManagementBackup(
    @Back onBack: () -> Unit,
) {
    val viewModel = getViewModel<WalletBackupViewModel>()
    val keyStore by viewModel.keyStore.observeAsState(initial = "")
    val privateKey by viewModel.privateKey.observeAsState(initial = "")
    BackupWalletScene(
        keyStore = keyStore,
        privateKey = privateKey,
        onBack = onBack,
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletManagementTransactionHistory,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun WalletManagementTransactionHistory(
    @Back onBack: () -> Unit,
) {
    val viewModel = getViewModel<WalletTransactionHistoryViewModel>()
    val transactions by viewModel.transactions.observeAsState()
    WalletTransactionHistoryScene(
        onBack = onBack,
        transactions = transactions,
        onSpeedUp = {
            // TODO:
        },
        onCancel = {
            // TODO:
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletManagementRename.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun WalletManagementRename(
    navController: NavController,
    @Path("id") walletId: String,
    @Path("name") walletName: String,
) {
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
                    popUpTo(CommonRoute.Main.Home.path) {
                        inclusive = false
                    }
                }
            )
        },
    )
}

internal fun navigateWalletCreate(
    type: CreateType,
    password: String,
    enableBiometric: Boolean,
    biometricEnableViewModel: BiometricEnableViewModel,
    context: Context,
    wallets: List<WalletData>,
    shouldShowLegalScene: Boolean,
    navController: NavController,
) {
    val route = if (shouldShowLegalScene) {
        WalletRoute.WalletIntroHostLegal(type.name)
    } else if (password.isNullOrEmpty()) {
        WalletRoute.WalletIntroHostPassword(type.name)
    } else if (!enableBiometric && biometricEnableViewModel.isSupported(context) && wallets.isEmpty()) {
        WalletRoute.WalletIntroHostFaceId(type.name)
    } else {
        WalletRoute.CreateOrImportWallet(type.name)
    }
    navController.navigate(
        route,
        navOptions {
            navController.currentDestination?.id?.let { popId ->
                popUpTo(popId) { inclusive = true }
            }
            launchSingleTop = true
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletIntroHostLegal.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun WalletIntroHostLegal(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("type") typeString: String,
) {
    val type = remember(typeString) { CreateType.valueOf(typeString) }
    val repo = get<SettingServices>()
    val walletRepo = get<IWalletRepository>()
    val wallets by walletRepo.wallets.observeAsState(emptyList())
    val password by repo.paymentPassword.observeAsState(initial = null)
    val enableBiometric by repo.biometricEnabled.observeAsState(initial = false)
    val biometricEnableViewModel: BiometricEnableViewModel = getViewModel()
    val context = LocalContext.current
    LegalScene(
        onBack = onBack,
        onAccept = {
            repo.setShouldShowLegalScene(false)
            val route = if (password.isNullOrEmpty()) {
                WalletRoute.WalletIntroHostPassword(type.name)
            } else if (!enableBiometric && biometricEnableViewModel.isSupported(context) && wallets.isEmpty()) {
                WalletRoute.WalletIntroHostFaceId(type.name)
            } else {
                WalletRoute.CreateOrImportWallet(type.name)
            }
            navController.navigate(
                route,
                navOptions {
                    navController.currentDestination?.id?.let { popId ->
                        popUpTo(popId) { inclusive = true }
                    }
                    launchSingleTop = true
                }
            )
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

@NavGraphDestination(
    route = WalletRoute.WalletIntroHostPassword.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun WalletIntroHostPassword(
    navController: NavController,
    @Path("type") typeString: String,
) {
    val type = remember(typeString) { CreateType.valueOf(typeString) }
    val repo = get<IWalletRepository>()
    val wallets by repo.wallets.observeAsState(emptyList())
    val enableBiometric by get<SettingServices>().biometricEnabled.observeAsState(initial = false)
    val biometricEnableViewModel: BiometricEnableViewModel = getViewModel()
    val context = LocalContext.current
    SetUpPaymentPassword(
        onNext = {
            if (!enableBiometric && biometricEnableViewModel.isSupported(context) && wallets.isEmpty()) {
                navController.navigate(WalletRoute.WalletIntroHostFaceId(type.name))
            } else {
                navController.navigate(WalletRoute.CreateOrImportWallet(type.name))
            }
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletIntroHostFaceId.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun WalletIntroHostFaceId(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("type") typeString: String,
) {
    val type = remember(typeString) { CreateType.valueOf(typeString) }
    BiometricsEnableScene(
        onBack = onBack,
        onEnable = { enabled ->
            if (enabled) {
                navController.navigate(WalletRoute.WalletIntroHostFaceIdEnableSuccess(type.name))
            } else {
                navController.navigate(WalletRoute.CreateOrImportWallet(type.name))
            }
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletIntroHostFaceIdEnableSuccess.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun WalletIntroHostFaceIdEnableSuccess(
    navController: NavController,
    @Path("type") typeString: String,
) {
    val type = remember(typeString) { CreateType.valueOf(typeString) }
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
                painterResource(id = R.drawable.ic_success),
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

@NavGraphDestination(
    route = WalletRoute.WalletIntroHostTouchId.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun WalletIntroHostTouchId(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("type") typeString: String,
) {
    val type = remember(typeString) { CreateType.valueOf(typeString) }
    TouchIdEnableScene(
        onBack = onBack,
        onEnable = {
            navController.navigate(WalletRoute.WalletIntroHostTouchIdEnableSuccess(type.name))
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletIntroHostTouchIdEnableSuccess.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun WalletIntroHostTouchIdEnableSuccess(
    navController: NavController,
    @Path("type") typeString: String,
) {
    val type = remember(typeString) { CreateType.valueOf(typeString) }
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
                painterResource(id = R.drawable.ic_success),
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

@NavGraphDestination(
    route = WalletRoute.CreateOrImportWallet.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun CreateOrImportWallet(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("type") typeString: String,
) {
    val type = remember(typeString) { CreateType.valueOf(typeString) }
    CreateOrImportWalletScene(
        navController = navController,
        onBack = onBack,
        type = type,
    )
}

@NavGraphDestination(
    route = WalletRoute.MultiChainWalletDialog,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun MultiChainWalletDialogRoute(
    @Back onBack: () -> Unit,
) {
    MultiChainWalletDialog(
        onBack = onBack,
    )
}

@NavGraphDestination(
    route = WalletRoute.UnlockWalletDialog.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun UnlockWalletDialog(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("target") target: String,
) {
    val viewModel = getViewModel<UnlockWalletViewModel>()
    val biometricEnable by viewModel.biometricEnabled.observeAsState(initial = false)
    val password by viewModel.password.observeAsState(initial = "")
    val passwordValid by viewModel.passwordValid.observeAsState(initial = false)
    val context = LocalContext.current
    UnlockWalletDialog(
        onBack = onBack,
        biometricEnabled = biometricEnable,
        password = password,
        onPasswordChanged = { viewModel.setPassword(it) },
        passwordValid = passwordValid,
        onConfirm = {
            if (biometricEnable) {
                viewModel.authenticate(
                    context = context,
                    onSuccess = {
                        navController.navigate(
                            target,
                            navOptions {
                                popUpTo(WalletRoute.UnlockWalletDialog.path) {
                                    inclusive = true
                                }
                            }
                        )
                    }
                )
            } else if (passwordValid) {
                navController.navigate(
                    target,
                    navOptions {
                        popUpTo(WalletRoute.UnlockWalletDialog.path) {
                            inclusive = true
                        }
                    }
                )
            }
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.WalletConnect.DApps,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun WalletConnectedDAppsModal() {
    val viewModel = getViewModel<DAppConnectedViewModel>()
    val apps by viewModel.apps.observeAsState()
    DAppConnectedModal(
        apps = apps,
        onDisconnect = {
            viewModel.disconnect(it)
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.EmptyTokenDialog.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun EmptyTokenDialogRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("tokenSymbol") tokenSymbol: String,
) {
    EmptyTokenDialog(
        tokenSymbol = tokenSymbol,
        onCancel = onBack,
        onBuy = {
            navController.navigate(deepLink = Uri.parse(Deeplinks.Labs.Transak))
        }
    )
}

fun NavController.transfer(walletNativeToken: WalletTokenData, tradableId: String? = null) {
    if (walletNativeToken.count <= BigDecimal.ZERO) {
        navigate(
            WalletRoute.EmptyTokenDialog(walletNativeToken.tokenData.symbol),
        )
    } else {
        navigate(WalletRoute.Transfer.SearchAddress.invoke(tradableId = tradableId))
    }
}
