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
package com.dimension.maskbook.persona.ui.scenes.register.recovery.remote

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.widget.EmailCodeInputModal
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.viewmodel.register.EmailRemoteBackupRecoveryViewModel
import com.dimension.maskbook.persona.viewmodel.register.PhoneRemoteBackupRecoveryViewModel
import com.dimension.maskbook.persona.viewmodel.register.RemoteBackupRecoveryViewModelBase
import moe.tlaster.koin.compose.getViewModel
import moe.tlaster.precompose.navigation.NavController
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_NoBackup,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun RegisterRecoveryRemoteBackupRecoveryRemoteBackupRecoveryNoBackup(
    @Back onBack: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = onBack,
        title = {
            Text(text = stringResource(R.string.scene_backup_remote_backup_no_backup_title))
        },
        text = {
            Text(text = stringResource(R.string.scene_backup_remote_backup_no_backup_message))
        },
        icon = {
            Image(
                painter = painterResource(id = com.dimension.maskbook.localization.R.drawable.ic_failed),
                contentDescription = null
            )
        },
        buttons = {
            PrimaryButton(
                onClick = onBack,
            ) {
                Text(text = stringResource(id = R.string.common_controls_ok))
            }
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email_Code.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun RegisterRecoveryRemoteBackupRecoveryRemoteBackupRecoveryEmailCode(
    navController: NavController,
    @Path("email") email: String,
) {
    val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
        when (it.target) {
            RemoteBackupRecoveryViewModelBase.NavigateTarget.NoBackup -> {
                navController.navigate(
                    PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_NoBackup
                )
            }
            RemoteBackupRecoveryViewModelBase.NavigateTarget.RestoreBackup -> {
                if (it is RemoteBackupRecoveryViewModelBase.BackupFileMetaNavigateArgs) {
                    navController.navigate(
                        PersonaRoute.Register.Recovery.LocalBackup.Loading(it.backupFileMeta.url, it.backupFileMeta.uploaded_at, it.backupFileMeta.abstract, email)
                    )
                }
            }
            else -> {
                Unit
            }
        }
    }
    val viewModel = getViewModel<EmailRemoteBackupRecoveryViewModel> {
        parametersOf(requestNavigate)
    }
    LaunchedEffect(Unit) {
        viewModel.startCountDown()
    }
    val canSend by viewModel.canSend.collectAsState(initial = false)
    val countDown by viewModel.countdown.collectAsState(initial = 60)
    val loading by viewModel.loading.collectAsState(initial = false)
    val code by viewModel.code.collectAsState(initial = "")
    val codeValid by viewModel.codeValid.collectAsState(initial = true)
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
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun RegisterRecoveryRemoteBackupRecoveryRemoteBackupRecoveryEmail(
    navController: NavController,
) {
    val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
        if (it is RemoteBackupRecoveryViewModelBase.StringNavigateArgs) {
            navController.navigate(PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email_Code(it.value))
        }
    }
    val viewModel = getViewModel<EmailRemoteBackupRecoveryViewModel> {
        parametersOf(requestNavigate)
    }
    val email by viewModel.value.collectAsState(initial = "")
    val emailValid by viewModel.valueValid.collectAsState(initial = true)
    val loading by viewModel.loading.collectAsState(initial = false)
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
                    navController.navigate(PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone) {
                        popUpTo(PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email) {
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

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone_Code.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun RegisterRecoveryRemoteBackupRecoveryRemoteBackupRecoveryPhoneCode(
    navController: NavController,
    @Path("phone") phone: String,
) {
    val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
        when (it.target) {
            RemoteBackupRecoveryViewModelBase.NavigateTarget.NoBackup -> {
                navController.navigate(
                    PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_NoBackup
                )
            }
            RemoteBackupRecoveryViewModelBase.NavigateTarget.RestoreBackup -> {
                if (it is RemoteBackupRecoveryViewModelBase.BackupFileMetaNavigateArgs) {
                    navController.navigate(
                        PersonaRoute.Register.Recovery.LocalBackup.Loading(it.backupFileMeta.url, it.backupFileMeta.uploaded_at, it.backupFileMeta.abstract, phone)
                    )
                }
            }
            else -> {
                Unit
            }
        }
    }
    val viewModel = getViewModel<PhoneRemoteBackupRecoveryViewModel> {
        parametersOf(requestNavigate)
    }
    LaunchedEffect(Unit) {
        viewModel.startCountDown()
    }
    val canSend by viewModel.canSend.collectAsState(initial = false)
    val countDown by viewModel.countdown.collectAsState(initial = 60)
    val loading by viewModel.loading.collectAsState(initial = false)
    val code by viewModel.code.collectAsState(initial = "")
    val codeValid by viewModel.codeValid.collectAsState(initial = true)
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
}

@NavGraphDestination(
    route = PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun RegisterRecoveryRemoteBackupRecoveryRemoteBackupRecoveryPhone(
    navController: NavController,
) {
    val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
        if (it is RemoteBackupRecoveryViewModelBase.StringNavigateArgs) {
            navController.navigate(PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone_Code(it.value))
        }
    }
    val viewModel = getViewModel<PhoneRemoteBackupRecoveryViewModel> {
        parametersOf(requestNavigate)
    }
    val regionCode by viewModel.regionCode.collectAsState(initial = "+86")
    val phone by viewModel.value.collectAsState(initial = "")
    val phoneValid by viewModel.valueValid.collectAsState(initial = true)
    val loading by viewModel.loading.collectAsState(initial = false)
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
                    navController.navigate(PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email) {
                        popUpTo(PersonaRoute.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone) {
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
