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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.wallet.ui.scenes.register.CreatePersonaModal
import com.dimension.maskbook.wallet.ui.scenes.register.CreatePersonaScene
import com.dimension.maskbook.wallet.ui.scenes.register.RegisterScene
import com.dimension.maskbook.wallet.ui.scenes.register.createidentity.CreateIdentityHost
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.IdentityScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.PrivateKeyScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryComplectedScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryHomeScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.local.RecoveryLocalHost
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.remote.remoteBackupRecovery
import com.dimension.maskbook.wallet.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.PrivateKeyViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
fun NavGraphBuilder.registerRoute(
    navController: NavController,
) {
    composable(
        WalletRoute.Register.Init,
    ) {
        val repository = get<PersonaServices>()
        val persona by repository.currentPersona.observeAsState(initial = null)
        LaunchedEffect(Unit) {
            snapshotFlow { persona }
                .distinctUntilChanged()
                .collect {
                    if (it != null) {
                        navController.navigate(
                            Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)),
                            navOptions {
                                popUpTo(WalletRoute.Register.Init) {
                                    inclusive = true
                                }
                            }
                        )
                    }
                }
        }
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
    composable(
        route = WalletRoute.Register.CreateIdentity.path,
        arguments = listOf(
            navArgument("personaName") { type = NavType.StringType }
        )
    ) {
        CreateIdentityHost(
            personaName = it.arguments?.getString("personaName").orEmpty(),
            onDone = {
                navController.navigate(
                    Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)),
                    navOptions = navOptions {
                        launchSingleTop = true
                        popUpTo(CommonRoute.Main.Home) {
                            inclusive = false
                        }
                    }
                )
            },
            onBack = {
                navController.popBackStack()
            }
        )
    }
    composable(
        WalletRoute.Register.WelcomeCreatePersona,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = Deeplinks.Wallet.Register.WelcomeCreatePersona
            }
        )
    ) {
        CreatePersonaScene(
            onBack = {
                navController.popBackStack()
            },
            onDone = { name ->
                navController.navigate(WalletRoute.Register.CreateIdentity(name))
            }
        )
    }
    bottomSheet(WalletRoute.Register.CreatePersona) {
        CreatePersonaModal(
            onDone = { name ->
                navController.navigate(WalletRoute.Register.CreateIdentity(name))
            }
        )
    }
    composable(
        WalletRoute.Register.Recovery.Home,
        deepLinks = listOf(
            navDeepLink { uriPattern = Deeplinks.Wallet.Recovery }
        )
    ) {
        RecoveryHomeScene(
            onBack = {
                navController.popBackStack()
            },
            onIdentity = {
                navController.navigate(WalletRoute.Register.Recovery.Identity)
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
    remoteBackupRecovery(navController)
    composable(
        WalletRoute.Register.Recovery.LocalBackup.RemoteBackupRecovery_RecoveryLocal.path,
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
                    navController.navigate(WalletRoute.Register.Recovery.Complected) {
                        popUpTo(WalletRoute.Register.Init) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
    composable(WalletRoute.Register.Recovery.LocalBackup.LocalBackup_PickFile) {
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
                    navController.popBackStack()
                }
            },
        )
        LaunchedEffect(Unit) {
            filePickerLauncher.launch(arrayOf("*/*"))
        }
    }
    composable(WalletRoute.Register.Recovery.Identity) {
        val viewModel: IdentityViewModel = getViewModel()
        val identity by viewModel.identity.observeAsState(initial = "")
        IdentityScene(
            identity = identity,
            onIdentityChanged = {
                viewModel.setIdentity(it)
            },
            onConfirm = {
                viewModel.onConfirm()
                navController.navigate(WalletRoute.Register.Recovery.Complected) {
                    popUpTo(WalletRoute.Register.Init) {
                        inclusive = true
                    }
                }
            },
            onBack = {
                navController.popBackStack()
            },
        )
    }
    composable(WalletRoute.Register.Recovery.PrivateKey) {
        val viewModel: PrivateKeyViewModel = getViewModel()
        val privateKey by viewModel.privateKey.observeAsState(initial = "")
        PrivateKeyScene(
            privateKey = privateKey,
            onPrivateKeyChanged = {
                viewModel.setPrivateKey(it)
            },
            onConfirm = {
                viewModel.onConfirm()
                navController.navigate(WalletRoute.Register.Recovery.Complected) {
                    popUpTo(WalletRoute.Register.Init) {
                        inclusive = true
                    }
                }
            },
            onBack = {
                navController.popBackStack()
            },
        )
    }
    composable(WalletRoute.Register.Recovery.Complected) {
        RecoveryComplectedScene(
            onBack = {
                navController.popBackStack()
            },
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
}
