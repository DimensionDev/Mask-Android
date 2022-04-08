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
package com.dimension.maskbook.persona.route

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.dimension.maskbook.common.ext.navOptions
import com.dimension.maskbook.common.ext.navigate
import com.dimension.maskbook.common.ext.navigateUri
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
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.export.error.PersonaAlreadyExitsError
import com.dimension.maskbook.persona.ui.scenes.register.CreatePersonaModal
import com.dimension.maskbook.persona.ui.scenes.register.CreatePersonaScene
import com.dimension.maskbook.persona.ui.scenes.register.RegisterScene
import com.dimension.maskbook.persona.ui.scenes.register.recovery.IdentityScene
import com.dimension.maskbook.persona.ui.scenes.register.recovery.PersonaAlreadyExitsDialog
import com.dimension.maskbook.persona.ui.scenes.register.recovery.PrivateKeyScene
import com.dimension.maskbook.persona.ui.scenes.register.recovery.RecoveryHomeScene
import com.dimension.maskbook.persona.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.persona.viewmodel.recovery.PrivateKeyViewModel
import kotlinx.coroutines.launch
import moe.tlaster.koin.compose.getViewModel
import moe.tlaster.precompose.navigation.NavController
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
    route = PersonaRoute.Register.Init,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterInit(
    navController: NavController,
) {
    RegisterScene(
        onCreateIdentity = {
            navController.navigate(PersonaRoute.Register.WelcomeCreatePersona)
        },
        onRecoveryAndSignIn = {
            navController.navigate(PersonaRoute.Register.Recovery.Home)
        },
        onSynchronization = {
            navController.navigate(PersonaRoute.Synchronization.Scan)
        },
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.WelcomeCreatePersona,
    deeplink = [
        Deeplinks.Persona.Register.WelcomeCreatePersona,
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
            navController.navigate(
                PersonaRoute.Register.CreateIdentity.Backup(
                    name,
                    isWelcome = true
                )
            )
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.CreatePersona,
    deeplink = [
        Deeplinks.Persona.Register.CreatePersona,
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
            navController.navigate(
                PersonaRoute.Register.CreateIdentity.Backup(
                    name,
                    isWelcome = false
                )
            )
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.Home,
    deeplink = [
        Deeplinks.Persona.Recovery
    ],
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterRecoveryHome(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                navController.navigate(
                    PersonaRoute.Register.Recovery.LocalBackup.Loading(it.toString(), System.currentTimeMillis(), null, null)
                )
            }
        },
    )
    RecoveryHomeScene(
        onBack = onBack,
        onIdentity = {
            navController.navigate(PersonaRoute.Register.Recovery.IdentityPersona)
        },
        onPrivateKey = {
            navController.navigate(PersonaRoute.Register.Recovery.PrivateKey)
        },
        onLocalBackup = {
            filePickerLauncher.launch(arrayOf("*/*"))
        },
        onRemoteBackup = {
            navController.navigate(PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email)
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.IdentityPersona,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun RegisterRecoveryIdentityPersona(
    navController: NavController,
) {
    CreatePersonaModal(
        onDone = { name ->
            navController.navigate(PersonaRoute.Register.Recovery.Identity(name))
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.Identity.path,
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
    val identity by viewModel.identity.collectAsState()
    val canConfirm by viewModel.canConfirm.collectAsState()
    val from = stringResource(R.string.scene_identity_mnemonic_import_title)
    val scope = rememberCoroutineScope()
    IdentityScene(
        identity = identity,
        onIdentityChanged = {
            viewModel.setIdentity(it)
        },
        canConfirm = canConfirm,
        onConfirm = {
            scope.launch {
                viewModel.confirm()
                    .onSuccess {
                        navController.navigate(PersonaRoute.Register.Recovery.Complected) {
                            popUpTo(PersonaRoute.Register.Init) {
                                inclusive = true
                            }
                        }
                    }.onFailure {
                        if (it is PersonaAlreadyExitsError) {
                            navController.navigate(PersonaRoute.Register.Recovery.AlreadyExists(from)) {
                                popUpTo(PersonaRoute.Register.Init) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.navigate(PersonaRoute.Register.Recovery.Failed) {
                                popUpTo(PersonaRoute.Register.Init) {
                                    inclusive = true
                                }
                            }
                        }
                    }
            }
        },
        onBack = onBack,
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.PrivateKey,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterRecoveryPrivateKey(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val viewModel: PrivateKeyViewModel = getViewModel()
    val privateKey by viewModel.privateKey.collectAsState()
    val canConfirm by viewModel.canConfirm.collectAsState()
    val scope = rememberCoroutineScope()
    val from = stringResource(R.string.scene_identity_privatekey_import_title)
    PrivateKeyScene(
        privateKey = privateKey,
        onPrivateKeyChanged = {
            viewModel.setPrivateKey(it)
        },
        canConfirm = canConfirm,
        onConfirm = {
            scope.launch {
                viewModel.confirm()
                    .onSuccess {
                        navController.navigate(PersonaRoute.Register.Recovery.Complected) {
                            popUpTo(PersonaRoute.Register.Init) {
                                inclusive = true
                            }
                        }
                    }.onFailure {
                        if (it is PersonaAlreadyExitsError) {
                            navController.navigate(PersonaRoute.Register.Recovery.AlreadyExists(from)) {
                                popUpTo(PersonaRoute.Register.Init) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.navigate(PersonaRoute.Register.Recovery.Failed) {
                                popUpTo(PersonaRoute.Register.Init) {
                                    inclusive = true
                                }
                            }
                        }
                    }
            }
        },
        onBack = onBack,
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.AlreadyExists.path,
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
                    popUpTo(PersonaRoute.Register.Init) {
                        inclusive = true
                    }
                }
            )
        },
        restoreFrom = restoreFrom
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.Complected,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterRecoveryComplected(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = onBack,
        title = {
            Text(stringResource(R.string.scene_Identity_restore_signin_success_title))
        },
        icon = {
            Image(painter = painterResource(R.drawable.ic_success), contentDescription = "")
        },
        buttons = {
            PrimaryButton(onClick = {
                navController.navigateUri(
                    Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)),
                    navOptions {
                        popUpTo(PersonaRoute.Register.Init) {
                            inclusive = true
                        }
                    }
                )
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.common_controls_done))
            }
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.Failed,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun RegisterRecoveryFailed(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = onBack,
        title = {
            Text(stringResource(R.string.scene_restore_titles_restore_failed))
        },
        icon = {
            Image(painter = painterResource(R.drawable.ic_failed), contentDescription = "")
        },
        buttons = {
            PrimaryButton(onClick = {
                navController.popBackStack()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.common_controls_ok))
            }
        }
    )
}
