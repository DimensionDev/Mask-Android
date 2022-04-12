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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.dimension.maskbook.common.ext.decodeBase64
import com.dimension.maskbook.common.ext.ifNullOrEmpty
import com.dimension.maskbook.common.ext.navOptions
import com.dimension.maskbook.common.ext.navigateUri
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.Persona
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.routeProcessor.annotations.Query
import com.dimension.maskbook.common.ui.barcode.ScanQrcodeScene
import com.dimension.maskbook.common.ui.scene.LoadingScene
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.export.error.PersonaAlreadyExitsError
import com.dimension.maskbook.persona.ui.scenes.register.recovery.PersonaAlreadyExitsDialog
import com.dimension.maskbook.persona.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.persona.viewmodel.recovery.PrivateKeyViewModel
import kotlinx.coroutines.launch
import moe.tlaster.koin.compose.getViewModel
import moe.tlaster.precompose.navigation.NavController
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
    route = PersonaRoute.Synchronization.Scan,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun SynchronizationScan(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    ScanQrcodeScene(
        onBack = onBack,
        onResult = {
            try {
                navController.navigateUri(
                    Uri.parse(it),
                    navOptions {
                        popUpTo(PersonaRoute.Synchronization.Scan) {
                            inclusive = true
                        }
                    }
                )
            } catch (e: Throwable) {
                navController.navigate(
                    PersonaRoute.Synchronization.Failed,
                    navOptions {
                        popUpTo(PersonaRoute.Synchronization.Scan) {
                            inclusive = true
                        }
                    }
                )
            }
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Synchronization.Success,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun SynchronizationSuccess(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = onBack,
        title = {
            Text(stringResource(R.string.scene_synchronization_success))
        },
        icon = {
            Image(painter = painterResource(R.drawable.ic_success), contentDescription = "")
        },
        buttons = {
            PrimaryButton(onClick = {
                navController.navigateUri(
                    Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)),
                    navOptions {
                        launchSingleTop = true
                        popUpTo(PersonaRoute.Synchronization.Success) {
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
    route = PersonaRoute.Synchronization.Failed,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun SynchronizationFailed(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = onBack,
        title = {
            Text(stringResource(R.string.scene_synchronization_failed))
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

@NavGraphDestination(
    route = PersonaRoute.Synchronization.Persona.AlreadyExists,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun SynchronizationPersonaAlreadyExists(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    PersonaAlreadyExitsDialog(
        onBack = onBack,
        onConfirm = {
            navController.navigateUri(
                Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)),
                navOptions {
                    launchSingleTop = true
                    popUpTo(PersonaRoute.Synchronization.Persona.AlreadyExists) {
                        inclusive = true
                    }
                }
            )
        },
        restoreFrom = stringResource(R.string.scene_identity_empty_synchronization)
    )
}

@NavGraphDestination(
    route = PersonaRoute.Synchronization.Persona.PrivateKey.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    deeplink = [Persona.PrivateKey.path]
)
@Composable
fun SynchronizationPersonaPrivateKey(
    navController: NavController,
    @Path("privateKey") key: String,
    @Query("nickname") nickname: String?,
) {
    val viewModel = getViewModel<PrivateKeyViewModel>()
    LoadingScene()
    LaunchedEffect(Unit) {
        launch {
            viewModel.setPrivateKey(key)
            navController.handleResult(viewModel.confirm(nickname = nickname.ifNullOrEmpty { "persona1" }))
        }
    }
}

@NavGraphDestination(
    route = PersonaRoute.Synchronization.Persona.Identity.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    deeplink = [Persona.Identity.path]
)
@Composable
fun SynchronizationIdentityPrivateKey(
    navController: NavController,
    @Path("identity") identity: String,
    @Query("nickname") nickname: String?,
) {
    val viewModel = getViewModel<IdentityViewModel> {
        parametersOf(nickname.ifNullOrEmpty { "persona1" })
    }
    LoadingScene()
    LaunchedEffect(Unit) {
        launch {
            viewModel.setIdentity(identity.decodeBase64())
            navController.handleResult(viewModel.confirm())
        }
    }
}

private fun NavController.handleResult(result: Result<Unit>) {
    result.onSuccess {
        navigate(
            PersonaRoute.Synchronization.Success,
            navOptions {
                currentBackStackEntry?.let { backStackEntry ->
                    popUpTo(backStackEntry.route.route) {
                        inclusive = true
                    }
                }
            }
        )
    }.onFailure {
        navigate(
            if (it is PersonaAlreadyExitsError)
                PersonaRoute.Synchronization.Persona.AlreadyExists
            else
                PersonaRoute.Synchronization.Failed,
            navOptions {
                currentBackStackEntry?.let { backStackEntry ->
                    popUpTo(backStackEntry.route.route) {
                        inclusive = true
                    }
                }
            }
        )
    }
}
