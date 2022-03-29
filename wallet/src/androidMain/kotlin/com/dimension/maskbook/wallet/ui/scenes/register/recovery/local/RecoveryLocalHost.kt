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
package com.dimension.maskbook.wallet.ui.scenes.register.recovery.local

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dimension.maskbook.common.ext.getNestedNavigationViewModel
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.routeProcessor.annotations.Query
import com.dimension.maskbook.common.ui.widget.BackMetaDisplay
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.viewmodel.recovery.RecoveryLocalViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.core.parameter.parametersOf

private const val GeneratedRouteName = "recoveryLocalRoute"

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.LocalBackup.Failed,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportFailedDialog(
    navController: NavController,
) {
    MaskDialog(
        onDismissRequest = {
        },
        title = {
            Text(text = stringResource(R.string.scene_restore_titles_unsupport_restore_data))
        },
        text = {
            Text(text = stringResource(R.string.scene_restore_check_unsupport_data))
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Text(text = stringResource(R.string.common_controls_ok))
            }
        },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_property_1_failed),
                contentDescription = null
            )
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.LocalBackup.Notification,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportNotificationDialog(
    navController: NavController,
) {
    MaskDialog(
        onDismissRequest = { },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        text = {
            Text(text = stringResource(R.string.scene_restore_tip_remote_restore_succeed))
        },
        buttons = {
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text(text = stringResource(R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(WalletRoute.Register.Recovery.Complected) {
                            popUpTo(WalletRoute.Register.Init) {
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text(text = stringResource(R.string.common_controls_confirm))
                }
            }
        }
    )
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.LocalBackup.Password.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportPasswordModal(
    @Path("uri") uri: String,
    @Query("account") account: String?,
    navController: NavController,
) {
    DisposableEffect(Unit) {
        onDispose {
            navController.popBackStack(
                WalletRoute.Register.Recovery.LocalBackup.Loading.path,
                inclusive = true,
            )
        }
    }
    val viewModel: RecoveryLocalViewModel = navController
        .getNestedNavigationViewModel(WalletRoute.Register.Recovery.LocalBackup.Route) {
            parametersOf(uri, account)
        }
    val password by viewModel.password.observeAsState(initial = "")
    val error by viewModel.passwordError.observeAsState(initial = false)
    MaskModal {
        Column {
            Text(text = stringResource(R.string.scene_set_backup_password_backup_password))
            Spacer(modifier = Modifier.height(8.dp))
            MaskPasswordInputField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = {
                    viewModel.setPassword(it)
                },
            )
            if (error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.scene_restore_tip_incorrect_backup_password),
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.confirmPassword() },
            ) {
                Text(text = stringResource(R.string.common_controls_next))
            }
        }
    }
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.LocalBackup.Success.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportSuccessScene(
    @Back onBack: () -> Unit,
    @Path("uri") uri: String,
    @Query("account") account: String?,
    navController: NavController,
) {
    val viewModel: RecoveryLocalViewModel = navController
        .getNestedNavigationViewModel(WalletRoute.Register.Recovery.LocalBackup.Route) {
            parametersOf(uri, account)
        }
    val meta by viewModel.meta.observeAsState(initial = null)
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.scene_restore_titles_restore_buckup))
                    },
                    navigationIcon = {
                        MaskBackButton {
                            onBack.invoke()
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ScaffoldPadding)
            ) {
                meta?.let { meta ->
                    BackMetaDisplay(meta)
                }
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.restore()
                        navController.navigate(WalletRoute.Register.Recovery.LocalBackup.Notification)
                    }
                ) {
                    Text(text = stringResource(R.string.scene_restore_buttonTitles_backup))
                }
            }
        }
    }
}

@NavGraphDestination(
    route = WalletRoute.Register.Recovery.LocalBackup.Loading.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportingScene(
    @Back onBack: () -> Unit,
    @Path("uri") uri: String,
    @Query("account") account: String?,
    navController: NavController,
) {
    val viewModel: RecoveryLocalViewModel = navController
        .getNestedNavigationViewModel(WalletRoute.Register.Recovery.LocalBackup.Route) {
            parametersOf(uri, account)
        }
    val state by viewModel.loadState.collectAsState()
    LaunchedEffect(Unit) {
        snapshotFlow { state }
            .distinctUntilChanged()
            .collect {
                when (it) {
                    RecoveryLocalViewModel.LoadState.Failed -> navController.navigate(WalletRoute.Register.Recovery.LocalBackup.Failed) {
                        popUpTo(WalletRoute.Register.Recovery.LocalBackup.Loading.path) {
                            inclusive = true
                        }
                    }
                    RecoveryLocalViewModel.LoadState.Success -> navController.navigate(
                        WalletRoute.Register.Recovery.LocalBackup.Success(
                            uri,
                            account
                        )
                    ) {
                        popUpTo(WalletRoute.Register.Recovery.LocalBackup.Loading.path) {
                            inclusive = true
                        }
                    }
                    RecoveryLocalViewModel.LoadState.RequirePassword -> {
                        navController.navigate(
                            WalletRoute.Register.Recovery.LocalBackup.Password(
                                uri,
                                account
                            )
                        )
                    }
                    else -> Unit
                }
            }
    }
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton {
                            onBack.invoke()
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Text(text = stringResource(R.string.common_loading))
            }
        }
    }
}
