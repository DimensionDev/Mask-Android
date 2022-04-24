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
package com.dimension.maskbook.persona.ui.scenes.register.createidentity

import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.dimension.maskbook.common.ext.encodeBase64
import com.dimension.maskbook.common.ext.getNestedNavigationViewModel
import com.dimension.maskbook.common.ext.navigateUri
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.viewmodel.DownloadQrCodeViewModel
import com.dimension.maskbook.persona.viewmodel.register.CreateIdentityViewModel
import moe.tlaster.precompose.navigation.NavController
import org.koin.core.parameter.parametersOf

private const val GeneratedRouteName = "createIdentityRoute"

@NavGraphDestination(
    route = PersonaRoute.Register.CreateIdentity.Backup.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun BackupRoute(
    navController: NavController,
    @Path("personaName") personaName: String,
    @Path("isWelcome") isWelcome: Boolean,
    @Back onBack: () -> Unit
) {
    val viewModel: CreateIdentityViewModel = navController
        .getNestedNavigationViewModel(PersonaRoute.Register.CreateIdentity.Route) {
            parametersOf(personaName)
        }
    val words by viewModel.words.observeAsState(emptyList())
    val showNext by viewModel.showNext.observeAsState()
    BackupIdentityScene(
        words = words.map { it.word },
        onRefreshWords = {
            viewModel.refreshWords()
        },
        onDownload = {
            viewModel.download()
            navController.navigate(
                PersonaRoute.DownloadQrCode(
                    idType = DownloadQrCodeViewModel.IdType.Mnemonic.name,
                    idBase64 = words.joinToString(separator = " ") { it.word }.encodeBase64(Base64.NO_WRAP)
                )
            )
        },
        onBack = onBack,
        onSkipOrNext = {
            viewModel.create()
            navController.navigate(PersonaRoute.Register.CreateIdentity.Confirm(personaName, isWelcome))
        },
        showNext = showNext,
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.CreateIdentity.Confirm.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ConfirmRoute(
    navController: NavController,
    @Path("personaName") personaName: String,
    @Path("isWelcome") isWelcome: Boolean,
) {
    MaskDialog(
        onDismissRequest = {
        },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_success),
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.common_alert_identity_create_title))
        },
        text = {
            Text(text = "You have successfully created your persona $personaName")
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigateUri(Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona))) {
                        launchSingleTop = true
                        if (isWelcome) {
                            popUpTo(PersonaRoute.Register.Init) {
                                inclusive = true
                            }
                        } else {
                            popUpTo(CommonRoute.Main.Home.path) {
                                inclusive = false
                            }
                        }
                    }
                },
            ) {
                Text(text = stringResource(R.string.common_controls_done))
            }
        },
    )
}
