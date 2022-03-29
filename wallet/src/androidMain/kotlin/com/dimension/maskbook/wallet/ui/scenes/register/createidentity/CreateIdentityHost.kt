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
package com.dimension.maskbook.wallet.ui.scenes.register.createidentity

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.dimension.maskbook.common.ext.getNestedNavigationViewModel
import com.dimension.maskbook.common.ext.navigate
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
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.ui.scenes.register.BackupIdentityScene
import com.dimension.maskbook.wallet.viewmodel.register.CreateIdentityViewModel
import org.koin.core.parameter.parametersOf

private const val GeneratedRouteName = "createIdentityRoute"

@NavGraphDestination(
    route = WalletRoute.Register.CreateIdentity.Backup.path,
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
        .getNestedNavigationViewModel(WalletRoute.Register.CreateIdentity.Route) {
            parametersOf(personaName)
        }
    val words by viewModel.words.observeAsState(emptyList())
    BackupIdentityScene(
        words = words.map { it.word },
        onRefreshWords = {
            viewModel.refreshWords()
        },
        onVerify = {
            navController.navigate(WalletRoute.Register.CreateIdentity.Verify(personaName, isWelcome))
        },
        onBack = onBack,
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.CreateIdentity.Verify.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun VerifyRoute(
    navController: NavController,
    @Path("personaName") personaName: String,
    @Path("isWelcome") isWelcome: Boolean,
    @Back onBack: () -> Unit
) {
    val viewModel: CreateIdentityViewModel = navController
        .getNestedNavigationViewModel(WalletRoute.Register.CreateIdentity.Route) {
            parametersOf(personaName)
        }
    val correct by viewModel.correct.observeAsState(initial = false)
    val selectedWords by viewModel.selectedWords.observeAsState(initial = emptyList())
    val wordsInRandomOrder by viewModel.wordsInRandomOrder.observeAsState(initial = emptyList())
    VerifyIdentityScene(
        words = wordsInRandomOrder,
        onBack = {
            viewModel.clearWords()
            onBack.invoke()
        },
        onClear = { viewModel.clearWords() },
        onConfirm = {
            navController.navigate(WalletRoute.Register.CreateIdentity.Confirm(personaName, isWelcome))
        },
        onWordSelected = {
            viewModel.selectWord(it)
        },
        selectedWords = selectedWords,
        correct = correct,
        onWordDeselected = {
            viewModel.deselectWord(it)
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.CreateIdentity.Confirm.path,
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
    val viewModel: CreateIdentityViewModel = navController
        .getNestedNavigationViewModel(WalletRoute.Register.CreateIdentity.Route) {
            parametersOf(personaName)
        }
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
            Text(text = stringResource(R.string.common_alert_identity_create_description))
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.confirm()
                    navController.navigate(Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona))) {
                        launchSingleTop = true
                        if (isWelcome) {
                            popUpTo(WalletRoute.Register.Init) {
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
