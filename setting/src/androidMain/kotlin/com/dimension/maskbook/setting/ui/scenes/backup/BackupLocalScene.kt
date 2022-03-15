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
package com.dimension.maskbook.setting.ui.scenes.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ui.widget.MaskCard
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.MetaItem
import com.dimension.maskbook.common.ui.widget.RouteHost
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.clickable
import com.dimension.maskbook.localization.R
import com.dimension.maskbook.setting.viewmodel.BackupLocalViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun BackupLocalHost(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
) {
    val viewModel: BackupLocalViewModel = getViewModel()
    val navController = rememberAnimatedNavController()
    val state by viewModel.state.observeAsState(initial = BackupLocalViewModel.State.Normal)
    LaunchedEffect(Unit) {
        snapshotFlow { state }
            .distinctUntilChanged()
            .collect {
                when (it) {
                    BackupLocalViewModel.State.Normal -> Unit
                    BackupLocalViewModel.State.Loading -> navController.navigate("Loading")
                    BackupLocalViewModel.State.Failed -> onFailure.invoke()
                    BackupLocalViewModel.State.Success -> onSuccess.invoke()
                }
            }
    }
    RouteHost(
        navController = navController,
        startDestination = "Main",
    ) {
        composable("Main") {
            BackupLocalScene(onBack = onBack, viewModel = viewModel)
        }
        composable("Loading") {
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
    }
}

@Composable
fun BackupLocalScene(
    onBack: () -> Unit,
    viewModel: BackupLocalViewModel,
) {
    val meta by viewModel.meta.observeAsState(initial = null)
    val password by viewModel.password.observeAsState(initial = "")
    val backupPasswordValid by viewModel.backupPasswordValid.observeAsState(initial = false)
    val withWallet by viewModel.withWallet.observeAsState(initial = false)
    val paymentPassword by viewModel.paymentPassword.observeAsState(initial = "")
    val paymentPasswordValid by viewModel.paymentPasswordValid.observeAsState(initial = false)
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.common_controls_back_up_locally))
                    },
                    navigationIcon = {
                        MaskBackButton(
                            onBack = onBack
                        )
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
                meta?.let { meta ->
                    MaskCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 0.dp,
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            MetaItem(
                                title = stringResource(R.string.scene_backup_restored_account),
                                value = meta.account
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            MetaItem(title = stringResource(R.string.tab_personas), value = meta.personas.toString())
                            Spacer(modifier = Modifier.height(16.dp))
                            MetaItem(
                                title = stringResource(R.string.scene_backup_restored_account),
                                value = meta.associatedAccount.toString()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            MetaItem(
                                title = stringResource(R.string.scene_backup_restored_post),
                                value = meta.encryptedPost.toString()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            MetaItem(
                                title = stringResource(R.string.scene_backup_restored_contacts),
                                value = meta.contacts.toString()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            MetaItem(
                                title = stringResource(R.string.scene_backup_restored_files),
                                value = meta.file.toString()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.clickable {
                            viewModel.setWithWallet(!withWallet)
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = withWallet, onCheckedChange = {
                            viewModel.setWithWallet(it)
                        })
                        Spacer(modifier = Modifier.width(10.dp))
                        MetaItem(
                            title = stringResource(R.string.scene_setting_local_backup_local_wallet),
                            value = meta.wallet.toString()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.scene_setting_backup_recovery_back_up_password))
                Spacer(modifier = Modifier.height(8.dp))
                MaskPasswordInputField(
                    value = password,
                    onValueChange = {
                        viewModel.setPassword(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = if (withWallet) ImeAction.Next else ImeAction.Done,
                )
                if (withWallet) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.scene_setting_general_setup_payment_password))
                    Spacer(modifier = Modifier.height(8.dp))
                    MaskPasswordInputField(
                        value = paymentPassword,
                        onValueChange = {
                            viewModel.setPaymentPassword(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.weight(1f))
                val filePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.CreateDocument(),
                    onResult = {
                        if (it != null) {
                            viewModel.save(it, withWallet)
                        }
                    },
                )
                PrimaryButton(
                    onClick = {
                        filePickerLauncher.launch("${System.currentTimeMillis()}.json")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = backupPasswordValid && (if (withWallet) paymentPasswordValid else true)
                ) {
                    Text(text = stringResource(R.string.scene_personas_action_backup))
                }
            }
        }
    }
}
