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
package com.dimension.maskbook.wallet.ui.scenes.register.recovery.remote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.dimension.maskbook.common.ext.encodeUrl
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ui.widget.EmailCodeInputModal
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.viewmodel.register.EmailRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.PhoneRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.RemoteBackupRecoveryViewModelBase
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.remoteBackupRecovery(
    navController: NavController
) {
    dialog(
        WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_NoBackup
    ) {
        MaskDialog(
            onDismissRequest = { navController.popBackStack() },
            title = { Text(text = "No Backup data found!") }
        )
    }
    bottomSheet(
        WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email_Code.path,
        arguments = listOf(navArgument("email") { type = NavType.StringType })
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("email")?.let { email ->
            val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                when (it.target) {
                    RemoteBackupRecoveryViewModelBase.NavigateTarget.NoBackup -> navController.navigate(
                        WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_NoBackup
                    )
                    RemoteBackupRecoveryViewModelBase.NavigateTarget.RestoreBackup -> navController.navigate(
                        WalletRoute.Register.Recovery.LocalBackup.RemoteBackupRecovery_RecoveryLocal(it.value)
                    )
                    else -> Unit
                }
            }
            val viewModel = getViewModel<EmailRemoteBackupRecoveryViewModel> {
                parametersOf(requestNavigate)
            }
            LaunchedEffect(Unit) {
                viewModel.startCountDown()
            }
            val canSend by viewModel.canSend.observeAsState(initial = false)
            val countDown by viewModel.countdown.observeAsState(initial = 60)
            val loading by viewModel.loading.observeAsState(initial = false)
            val code by viewModel.code.observeAsState(initial = "")
            val codeValid by viewModel.codeValid.observeAsState(initial = true)
            EmailCodeInputModal(
                email = email,
                code = code,
                onCodeChange = {
                    viewModel.setCode(it)
                },
                canSend = canSend,
                codeValid = codeValid,
                countDown = countDown,
                buttonEnabled = loading,
                onSendCode = { viewModel.sendCode(email) },
                onVerify = { viewModel.verifyCode(code, email, skipValidate = true) },
                title = stringResource(R.string.scene_restore_titles_recovery_with_email)
            )
        } ?: run {
            navController.popBackStack()
        }
    }
    bottomSheet(WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email) {
        val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
            navController.navigate(WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email_Code(it.value))
        }
        val viewModel = getViewModel<EmailRemoteBackupRecoveryViewModel> {
            parametersOf(requestNavigate)
        }
        val email by viewModel.value.observeAsState(initial = "")
        val emailValid by viewModel.valueValid.observeAsState(initial = true)
        val loading by viewModel.loading.observeAsState(initial = false)
        MaskModal(
            title = {
                Text(text = stringResource(R.string.scene_restore_titles_recovery_with_email))
            }
        ) {
            Column {
                Text(text = stringResource(R.string.scene_backup_backup_verify_field_email))
                Spacer(modifier = Modifier.height(8.dp))
                MaskInputField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = {
                        viewModel.setValue(it)
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                if (!emailValid) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.scene_restore_tip_invalid_email_address), color = Color.Red)
                }
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.sendCode(email)
                    },
                    enabled = emailValid && !loading && email.isNotEmpty(),
                ) {
                    Text(text = stringResource(R.string.common_controls_next))
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone) {
                            popUpTo(WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email) {
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text(text = stringResource(R.string.scene_restore_buttonTitles_email))
                }
            }
        }
    }
    bottomSheet(
        WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone_Code.path,
        arguments = listOf(navArgument("phone") { type = NavType.StringType })
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("phone")?.let { phone ->
            val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                when (it.target) {
                    RemoteBackupRecoveryViewModelBase.NavigateTarget.NoBackup -> navController.navigate(
                        WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_NoBackup
                    )
                    RemoteBackupRecoveryViewModelBase.NavigateTarget.RestoreBackup -> navController.navigate(
                        WalletRoute.Register.Recovery.LocalBackup.RemoteBackupRecovery_RecoveryLocal(it.value)
                    )
                    else -> Unit
                }
            }
            val viewModel = getViewModel<PhoneRemoteBackupRecoveryViewModel> {
                parametersOf(requestNavigate)
            }
            LaunchedEffect(Unit) {
                viewModel.startCountDown()
            }
            val canSend by viewModel.canSend.observeAsState(initial = false)
            val countDown by viewModel.countdown.observeAsState(initial = 60)
            val loading by viewModel.loading.observeAsState(initial = false)
            val code by viewModel.code.observeAsState(initial = "")
            val codeValid by viewModel.codeValid.observeAsState(initial = true)
            MaskModal(
                title = {
                    Text(text = stringResource(R.string.scene_restore_titles_recovery_with_mobile))
                }
            ) {
                Column {
                    Text(text = stringResource(R.string.scene_backup_validation_code))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MaskInputField(
                            modifier = Modifier.weight(1f),
                            value = code,
                            onValueChange = {
                                viewModel.setCode(it)
                            },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PrimaryButton(
                            onClick = {
                                viewModel.sendCode(phone)
                            },
                            enabled = canSend && !loading,
                        ) {
                            if (canSend) {
                                Text(text = stringResource(R.string.common_controls_resend))
                            } else {
                                Text(text = countDown.toString() + "s")
                            }
                        }
                    }
                    if (!codeValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.scene_restore_tip_invalid_validationcode),
                            color = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${stringResource(R.string.scene_restore_tip_mobile_validationcode)} $phone"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.verifyCode(code, phone, skipValidate = true)
                        },
                        enabled = code.isNotEmpty() && !loading,
                    ) {
                        Text(text = stringResource(R.string.common_controls_confirm))
                    }
                }
            }
        } ?: run {
            navController.popBackStack()
        }
    }
    bottomSheet(WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone) {
        val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
            val a = it.value.encodeUrl()
            navController.navigate(WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone_Code(it.value))
        }
        val viewModel = getViewModel<PhoneRemoteBackupRecoveryViewModel> {
            parametersOf(requestNavigate)
        }
        val regionCode by viewModel.regionCode.observeAsState(initial = "+86")
        val phone by viewModel.value.observeAsState(initial = "")
        val phoneValid by viewModel.valueValid.observeAsState(initial = true)
        val loading by viewModel.loading.observeAsState(initial = false)
        MaskModal(
            title = {
                Text(text = stringResource(R.string.scene_restore_titles_recovery_with_mobile))
            }
        ) {
            Column {
                Text(text = stringResource(R.string.scene_backup_backup_verify_field_phone))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MaskInputField(
                        modifier = Modifier.weight(1f),
                        value = regionCode,
                        onValueChange = { viewModel.setRegionCode(it) },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MaskInputField(
                        modifier = Modifier.weight(4f),
                        value = phone,
                        onValueChange = {
                            viewModel.setValue(it)
                        },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                }
                if (!phoneValid) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.scene_restore_tip_invalid_mobile_number), color = Color.Red)
                }
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.sendCode(regionCode + phone)
                    },
                    enabled = phoneValid && !loading && phone.isNotEmpty(),
                ) {
                    Text(text = stringResource(R.string.common_controls_next))
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email) {
                            popUpTo(WalletRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone) {
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text(text = stringResource(R.string.scene_restore_titles_recovery_with_email))
                }
            }
        }
    }
}
