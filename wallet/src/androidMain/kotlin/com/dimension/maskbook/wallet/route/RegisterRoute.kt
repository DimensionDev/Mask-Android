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

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.dimension.maskbook.common.ext.observeAsState
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
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.scenes.register.CreatePersonaModal
import com.dimension.maskbook.wallet.ui.scenes.register.CreatePersonaScene
import com.dimension.maskbook.wallet.ui.scenes.register.RegisterScene
import com.dimension.maskbook.wallet.ui.scenes.register.createidentity.CreateIdentityHost
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.IdentityScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.PersonaAlreadyExitsDialog
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.PrivateKeyScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryComplectedScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryHomeScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.local.RecoveryLocalHost
import com.dimension.maskbook.wallet.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.PrivateKeyViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
    route = WalletRoute.Register.Init,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterInit(
    navController: NavController,
) {
    RegisterScene(
        onCreateIdentity = {
            navController.navigate(WalletRoute.Register.WelcomeCreatePersona)
        },
        onRecoveryAndSignIn = {
            navController.navigate(WalletRoute.Register.Recovery.Home)
        },
        onSynchronization = {
        },
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.CreateIdentity.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterCreateIdentity(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("personaName") personaName: String,
) {
    CreateIdentityHost(
        personaName = personaName,
        onDone = {
            navController.navigate(
                Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)),
                navOptions = navOptions {
                    launchSingleTop = true
                    popUpTo(CommonRoute.Main.Home.path) {
                        inclusive = false
                    }
                }
            )
        },
        onBack = onBack,
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.WelcomeCreatePersona,
    deeplink = [
        Deeplinks.Wallet.Register.WelcomeCreatePersona,
    ],
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun WelcomeCreatePersona(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    CreatePersonaScene(
        onBack = onBack,
        onDone = { name ->
            navController.navigate(WalletRoute.Register.CreateIdentity(name))
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.CreatePersona,
    deeplink = [
        Deeplinks.Wallet.Register.CreatePersona,
    ],
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun CreatePersona(
    navController: NavController,
) {
    CreatePersonaModal(
        onDone = { name ->
            navController.navigate(WalletRoute.Register.CreateIdentity(name))
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.Home,
    deeplink = [
        Deeplinks.Wallet.Recovery
    ],
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterRecoveryHome(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    RecoveryHomeScene(
        onBack = onBack,
        onIdentity = {
            navController.navigate(WalletRoute.Register.Recovery.IdentityPersona)
        },
        onPrivateKey = {
            navController.navigate(WalletRoute.Register.Recovery.PrivateKey)
        },
        onLocalBackup = {
            navController.navigate(WalletRoute.Register.Recovery.LocalBackup.LocalBackup_PickFile)
        },
        onRemoteBackup = {
            navController.navigate(WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email)
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.LocalBackup.RemoteBackupRecovery_RecoveryLocal.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RecoveryLocalBackupRemoteBackupRecoveryRecoveryLocal(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("uri") uriString: String,
) {
    val uri = remember(uriString) { Uri.parse(uriString) }
    RecoveryLocalHost(
        uri = uri,
        onBack = onBack,
        onConfirm = {
            navController.navigate(WalletRoute.Register.Recovery.Complected) {
                popUpTo(WalletRoute.Register.Init) {
                    inclusive = true
                }
            }
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.LocalBackup.LocalBackup_PickFile,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterRecoveryLocalBackupLocalBackupPickFile(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                navController.navigate(
                    WalletRoute.Register.Recovery.LocalBackup.RemoteBackupRecovery_RecoveryLocal(it.toString())

                ) {
                    popUpTo(WalletRoute.Register.Recovery.LocalBackup.LocalBackup_PickFile) {
                        inclusive = true
                    }
                }
            } else {
                onBack.invoke()
            }
        },
    )
    LaunchedEffect(Unit) {
        filePickerLauncher.launch(arrayOf("*/*"))
    }
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.IdentityPersona,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun RegisterRecoveryIdentityPersona(
    navController: NavController,
) {
    CreatePersonaModal(
        onDone = { name ->
            navController.navigate(WalletRoute.Register.Recovery.Identity(name))
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.Identity.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterRecoveryIdentity(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("name") name: String,
) {
    val viewModel: IdentityViewModel = getViewModel {
        parametersOf(name)
    }
    val identity by viewModel.identity.observeAsState()
    val canConfirm by viewModel.canConfirm.observeAsState()
    val from = stringResource(R.string.scene_identity_mnemonic_import_title)
    IdentityScene(
        identity = identity,
        onIdentityChanged = {
            viewModel.setIdentity(it)
        },
        canConfirm = canConfirm,
        onConfirm = {
            viewModel.onConfirm(
                onSuccess = {
                    navController.navigate(WalletRoute.Register.Recovery.Complected) {
                        popUpTo(WalletRoute.Register.Init) {
                            inclusive = true
                        }
                    }
                },
                onAlreadyExists = {
                    navController.navigate(WalletRoute.Register.Recovery.AlreadyExists(from))
                }
            )
        },
        onBack = onBack,
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.AlreadyExists.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun RegisterRecoveryAlreadyExists(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("restoreFrom") restoreFrom: String,
) {
    PersonaAlreadyExitsDialog(
        onBack = onBack,
        onConfirm = {
            navController.navigate(
                Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)),
                navOptions {
                    popUpTo(WalletRoute.Register.Init) {
                        inclusive = true
                    }
                }
            )
        },
        restoreFrom = restoreFrom
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.PrivateKey,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterRecoveryPrivateKey(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val viewModel: PrivateKeyViewModel = getViewModel()
    val privateKey by viewModel.privateKey.observeAsState()
    val canConfirm by viewModel.canConfirm.observeAsState()
    PrivateKeyScene(
        privateKey = privateKey,
        onPrivateKeyChanged = {
            viewModel.setPrivateKey(it)
        },
        canConfirm = canConfirm,
        onConfirm = {
            viewModel.onConfirm()
            navController.navigate(WalletRoute.Register.Recovery.Complected) {
                popUpTo(WalletRoute.Register.Init) {
                    inclusive = true
                }
            }
        },
        onBack = onBack,
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.Complected,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterRecoveryComplected(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    RecoveryComplectedScene(
        onBack = onBack,
        onConfirm = {
            navController.navigate(
                Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)),
                navOptions {
                    popUpTo(WalletRoute.Register.Init) {
                        inclusive = true
                    }
                }
            )
        },
    )
}
