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
package com.dimension.maskbook.persona.ui.scenes.register.recovery.local

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.getNestedNavigationViewModel
import com.dimension.maskbook.common.ext.humanizeFileSize
import com.dimension.maskbook.common.ext.humanizeTimestamp
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
import com.dimension.maskbook.common.ui.scene.SetUpPaymentPassword
import com.dimension.maskbook.common.ui.widget.BackMetaDisplay
import com.dimension.maskbook.common.ui.widget.MaskCard
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
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.viewmodel.recovery.RecoveryLocalViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import moe.tlaster.precompose.navigation.NavController
import org.koin.core.parameter.parametersOf
import java.io.File

private const val GeneratedRouteName = "recoveryLocalRoute"

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.LocalBackup.Failed,
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
                painterResource(id = R.drawable.ic_failed),
                contentDescription = null
            )
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.LocalBackup.Notification,
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
                painterResource(id = R.drawable.ic_note),
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
                        navController.navigate(PersonaRoute.Register.Recovery.Complected) {
                            popUpTo(PersonaRoute.Register.Init) {
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
    route = PersonaRoute.Register.Recovery.LocalBackup.Password.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportPasswordScene(
    @Path("uri") uri: String,
    @Query("uploaded_at") uploadedAt: Long?,
    @Query("abstract") abstract: String?,
    @Query("account") account: String?,
    navController: NavController,
) {
    val viewModel: RecoveryLocalViewModel = navController
        .getNestedNavigationViewModel(PersonaRoute.Register.Recovery.LocalBackup.Route) {
            parametersOf(uri, account)
        }
    val state by viewModel.loadState.collectAsState()
    LaunchedEffect(Unit) {
        snapshotFlow { state }
            .distinctUntilChanged()
            .collect {
                when (it) {
                    RecoveryLocalViewModel.LoadState.Failed -> navController.navigate(PersonaRoute.Register.Recovery.LocalBackup.Failed) {
                        popUpTo(PersonaRoute.Register.Recovery.LocalBackup.Loading.path) {
                            inclusive = true
                        }
                    }
                    RecoveryLocalViewModel.LoadState.Success -> navController.navigate(
                        PersonaRoute.Register.Recovery.LocalBackup.Success(
                            uri,
                            account
                        )
                    ) {
                        popUpTo(PersonaRoute.Register.Recovery.LocalBackup.Loading.path) {
                            inclusive = true
                        }
                    }
                    else -> Unit
                }
            }
    }
    val passwordValid by viewModel.passwordValid.collectAsState(false)
    val password by viewModel.password.collectAsState()
    val error by viewModel.passwordError.collectAsState()
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(
                            onBack = {
                                navController.popBackStack(
                                    PersonaRoute.Register.Recovery.LocalBackup.Loading.path,
                                    inclusive = true,
                                )
                            }
                        )
                    },
                    title = {
                        Text(text = "Restore backup")
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ScaffoldPadding),
            ) {
                MaskCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val file = remember(uri) { File(uri) }
                        val fileName = remember(file) { file.name }
                        Column {
                            Text(abstract ?: fileName, style = MaterialTheme.typography.button)
                            Text(uploadedAt?.humanizeTimestamp() ?: "")
                        }
                        val size = remember(file) {
                            file.length().humanizeFileSize()
                        }
                        Text(
                            text = size,
                            style = MaterialTheme.typography.button,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Backup password")
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
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .background(Color(0xFFAFC3E1).copy(alpha = 0.15f))
                        .clip(RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painterResource(R.drawable.ic_info_circle),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(text = "To decrypt the backup, enter the backup password you used when you back up.")
                }
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.confirmPassword() },
                    enabled = passwordValid,
                ) {
                    Text(text = stringResource(R.string.common_controls_next))
                }
            }
        }
    }
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.LocalBackup.ConfirmPassword.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportWalletConfirmPasswordModal(
    navController: NavController,
    @Path("uri") uri: String,
    @Query("account") account: String?,
) {
    val viewModel: RecoveryLocalViewModel = navController
        .getNestedNavigationViewModel(PersonaRoute.Register.Recovery.LocalBackup.Route) {
            parametersOf(uri, account)
        }
    VerifyPaymentPasswordModal(
        onNext = {
            viewModel.restore()
            navController.navigate(PersonaRoute.Register.Recovery.LocalBackup.Notification)
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.LocalBackup.SetupPassword.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportWalletCreatePasswordModal(
    navController: NavController,
    @Path("uri") uri: String,
    @Query("account") account: String?,
) {
    val viewModel: RecoveryLocalViewModel = navController
        .getNestedNavigationViewModel(PersonaRoute.Register.Recovery.LocalBackup.Route) {
            parametersOf(uri, account)
        }
    SetUpPaymentPassword(
        onNext = {
            viewModel.restore()
            navController.navigate(PersonaRoute.Register.Recovery.LocalBackup.Notification)
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.LocalBackup.Wallet.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportWalletScene(
    @Path("uri") uri: String,
    @Query("account") account: String?,
    navController: NavController,
) {
    val viewModel: RecoveryLocalViewModel = navController
        .getNestedNavigationViewModel(PersonaRoute.Register.Recovery.LocalBackup.Route) {
            parametersOf(uri, account)
        }
    val paymentPassword by viewModel.paymentPassword.collectAsState(initial = null)
    val file by viewModel.file.observeAsState(initial = null)
    MaskModal(
        title = { Text(text = "Wallets for recovery") },
    ) {
        Column {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                file?.wallets?.let { wallets ->
                    if (wallets.isEmpty()) {
                        Text(text = "No wallets found")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            items(wallets) { wallet ->
                                Column {
                                    Text(text = wallet.name)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = wallet.address)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (paymentPassword.isNullOrEmpty()) {
                        navController.navigate(
                            PersonaRoute.Register.Recovery.LocalBackup.SetupPassword(
                                uri,
                                account
                            )
                        )
                    } else {
                        navController.navigate(
                            PersonaRoute.Register.Recovery.LocalBackup.ConfirmPassword(
                                uri,
                                account
                            )
                        )
                    }
                }
            ) {
                Text(text = stringResource(R.string.common_controls_next))
            }
        }
    }
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.LocalBackup.Success.path,
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
        .getNestedNavigationViewModel(PersonaRoute.Register.Recovery.LocalBackup.Route) {
            parametersOf(uri, account)
        }
    val meta by viewModel.meta.collectAsState(initial = null)
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
                        if (meta?.wallet != 0) {
                            navController.navigate(
                                PersonaRoute.Register.Recovery.LocalBackup.Wallet(
                                    uri,
                                    account
                                )
                            )
                        } else {
                            viewModel.restore()
                            navController.navigate(PersonaRoute.Register.Recovery.LocalBackup.Notification)
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.scene_restore_buttonTitles_backup))
                }
            }
        }
    }
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.LocalBackup.Loading.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    generatedFunctionName = GeneratedRouteName
)
@Composable
fun ImportingScene(
    @Back onBack: () -> Unit,
    @Path("uri") uri: String,
    @Query("uploaded_at") uploadedAt: Long?,
    @Query("abstract") abstract: String?,
    @Query("account") account: String?,
    navController: NavController,
) {
    val viewModel: RecoveryLocalViewModel = navController
        .getNestedNavigationViewModel(PersonaRoute.Register.Recovery.LocalBackup.Route) {
            parametersOf(uri, account)
        }
    val state by viewModel.loadState.collectAsState()
    LaunchedEffect(Unit) {
        snapshotFlow { state }
            .distinctUntilChanged()
            .collect {
                when (it) {
                    RecoveryLocalViewModel.LoadState.Failed -> navController.navigate(PersonaRoute.Register.Recovery.LocalBackup.Failed) {
                        popUpTo(PersonaRoute.Register.Recovery.LocalBackup.Loading.path) {
                            inclusive = true
                        }
                    }
                    RecoveryLocalViewModel.LoadState.Success -> navController.navigate(
                        PersonaRoute.Register.Recovery.LocalBackup.Success(
                            uri,
                            account
                        )
                    ) {
                        popUpTo(PersonaRoute.Register.Recovery.LocalBackup.Loading.path) {
                            inclusive = true
                        }
                    }
                    RecoveryLocalViewModel.LoadState.RequirePassword -> {
                        navController.navigate(
                            PersonaRoute.Register.Recovery.LocalBackup.Password(
                                uri,
                                uploadedAt,
                                abstract,
                                account,
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
