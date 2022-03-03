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
package com.dimension.maskbook.setting.route

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.routeProcessor.annotations.Query
import com.dimension.maskbook.common.ui.widget.EmailCodeInputModal
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.localization.R
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.setting.repository.ISettingsRepository
import com.dimension.maskbook.setting.ui.scenes.PhoneCodeInputModal
import com.dimension.maskbook.setting.ui.scenes.backup.BackupCloudScene
import com.dimension.maskbook.setting.ui.scenes.backup.BackupLocalHost
import com.dimension.maskbook.setting.ui.scenes.backup.BackupPasswordInputModal
import com.dimension.maskbook.setting.ui.scenes.backup.BackupSelectionModal
import com.dimension.maskbook.setting.viewmodel.BackupCloudExecuteViewModel
import com.dimension.maskbook.setting.viewmodel.BackupMergeConfirmViewModel
import com.dimension.maskbook.setting.viewmodel.EmailBackupViewModel
import com.dimension.maskbook.setting.viewmodel.PhoneBackupViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupData_Cloud_Success,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun BackupDataCloudSuccess(
    navController: NavController,
) {
    MaskDialog(
        onDismissRequest = {
            navController.popBackStack()
        },
        title = {
            Text(text = stringResource(R.string.common_alert_local_backup_backup_completed))
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Text(text = stringResource(R.string.common_controls_done))
            }
        },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_snccess),
                contentDescription = null
            )
        }
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupData_Cloud_Failed,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun BackupDataCloudFailed(
    navController: NavController,
) {
    MaskDialog(
        onDismissRequest = {
            navController.popBackStack()
        },
        title = {
            Text(text = stringResource(R.string.common_alert_local_backup_backup_failed))
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Text(text = "OK")
            }
        },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_failed),
                contentDescription = null
            )
        }
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupData_BackupCloud.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun BackupDataBackupCould(
    navController: NavController,
    @Path("type") type: String,
    @Path("value") value: String,
    @Path("code") code: String,
) {
    BackupCloudScene(
        onBack = {
            navController.popBackStack()
        },
        onConfirm = {
            navController.navigate(SettingRoute.BackupData.BackupData_BackupCloud_Execute(it, type, value, code)) {
                navController.currentBackStackEntry?.let { backStackEntry ->
                    popUpTo(backStackEntry.destination.id) {
                        inclusive = true
                    }
                }
            }
        }
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupData_BackupCloud_Execute.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun BackupDataBackupCouldExecute(
    navController: NavController,
    @Path("withWallet") withWallet: Boolean,
    @Path("type") type: String,
    @Path("value") value: String,
    @Path("code") code: String,
) {
    val viewModel = getViewModel<BackupCloudExecuteViewModel>()

    LaunchedEffect(Unit) {
        val result = viewModel.execute(
            code = code,
            type = type,
            account = value,
            withWallet = withWallet,
        )
        if (result) {
            navController.navigate(SettingRoute.BackupData.BackupData_Cloud_Success) {
                navController.currentBackStackEntry?.let { backStackEntry ->
                    popUpTo(backStackEntry.destination.id) {
                        inclusive = true
                    }
                }
            }
        } else {
            navController.navigate(SettingRoute.BackupData.BackupData_Cloud_Failed) {
                navController.currentBackStackEntry?.let { backStackEntry ->
                    popUpTo(backStackEntry.destination.id) {
                        inclusive = true
                    }
                }
            }
        }
    }

    MaskScene {
        MaskScaffold {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = stringResource(R.string.scene_setting_local_backup_loading_text))
            }
        }
    }
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupSelection_NoEmailAndPhone,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun BackupSelectionNoEmailAndPhone(
    navController: NavController,
) {
    MaskDialog(
        onDismissRequest = { navController.popBackStack() },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        title = { Text(text = "Bind your email or phone") },
        text = { Text(text = "Please bind your email or phone number before you back up to cloud. ") },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.popBackStack() },
            ) {
                Text(text = "OK")
            }
        }
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupData_BackupMerge_Confirm_Success.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun BackupDataBackupMergeConfirmSuccess(
    navController: NavController,
    @Path("type") type: String,
    @Path("value") value: String,
    @Path("code") code: String,
) {
    MaskDialog(
        onDismissRequest = { },
        text = {
            Text(text = "You have successfully merged your cloud backup to the local data. You can now proceed to back up.")
        },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_snccess),
                contentDescription = null
            )
        },
        buttons = {
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.popBackStack(SettingRoute.BackupData.BackupSelection, inclusive = true)
                    }
                ) {
                    Text(text = stringResource(R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(
                            SettingRoute.BackupData.BackupData_BackupCloud(type, value, code),
                        ) {
                            popUpTo(SettingRoute.BackupData.BackupSelection) {
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text(text = "To back up")
                }
            }
        }
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupData_BackupMerge_Confirm.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun BackupDataBackupMergeConfirm(
    navController: NavController,
    @Path("type") type: String,
    @Path("value") value: String,
    @Path("code") code: String,
    @Query("download_url") downloadUrl: String?,
    @Query("size") size: Long?,
    @Query("uploaded_at") uploadedAt: Long?,
    @Query("abstract") abstract: String?,
) {
    val onDone: () -> Unit = {
        navController.navigate(
            SettingRoute.BackupData.BackupData_BackupMerge_Confirm_Success(
                type,
                value,
                code
            )
        ) {
            popUpTo(SettingRoute.BackupData.BackupData_BackupMerge.path) {
                inclusive = true
            }
        }
    }

    val viewModel = getViewModel<BackupMergeConfirmViewModel> {
        parametersOf(onDone)
    }

    val passwordValid by viewModel.passwordValid.observeAsState(initial = false)
    val loading by viewModel.loading.observeAsState(initial = false)
    val password by viewModel.backupPassword.observeAsState(initial = "")

    MaskModal(
        title = {
            Text(text = stringResource(R.string.scene_backup_merge_to_local_title))
        }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colors.surface,
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = abstract.orEmpty())
                    Text(text = uploadedAt.toString())
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = size.toString())
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.scene_set_backup_password_backup_password))
            MaskInputField(
                value = password,
                onValueChange = { viewModel.setBackupPassword(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.confirm(type, downloadUrl.orEmpty())
                },
                enabled = passwordValid && !loading
            ) {
                Text(text = stringResource(R.string.common_controls_merge_to_local_data))
            }
        }
    }
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupData_BackupMerge.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun BackupDataBackupMerge(
    navController: NavController,
    @Path("type") type: String,
    @Path("value") value: String,
    @Path("code") code: String,
    @Query("download_url") downloadUrl: String?,
    @Query("size") size: Long?,
    @Query("uploaded_at") uploadedAt: Long?,
    @Query("abstract") abstract: String?,
) {
    MaskModal {
        Column {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null,
                alignment = Alignment.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colors.surface,
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = abstract.orEmpty())
                    Text(text = uploadedAt.toString())
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = size.toString())
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.scene_backup_remote_backup_actions_view_tips))
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate(
                        SettingRoute.BackupData.BackupData_BackupMerge_Confirm(
                            type,
                            value,
                            code,
                            download_url = downloadUrl.orEmpty(),
                            size = size,
                            uploaded_at = uploadedAt,
                            abstract = abstract
                        ),
                    )
                },
            ) {
                Text(text = stringResource(R.string.common_controls_merge_and_back_up))
            }
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(onClick = {
                navController.navigate(SettingRoute.BackupData.BackupData_BackupCloud(type, value, code))
            }) {
                Text(text = stringResource(R.string.common_controls_back_up))
            }
        }
    }
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupSelection_Email.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun BackupSelectionEmail(
    navController: NavController,
    @Path("email") email: String,
) {
    val scope = rememberCoroutineScope()

    val repository = get<PersonaServices>()
    val persona by repository.currentPersona.observeAsState(initial = null)
    val phone = persona?.phone

    val viewModel = getViewModel<EmailBackupViewModel>()
    val code by viewModel.code.observeAsState()
    val valid by viewModel.codeValid.observeAsState()
    val loading by viewModel.loading.observeAsState()
    val canSend by viewModel.canSend.observeAsState()
    val countDown by viewModel.countdown.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.startCountDown()
        viewModel.sendCodeNow(email)
    }

    EmailCodeInputModal(
        email = email,
        buttonEnabled = loading,
        title = stringResource(R.string.scene_backup_backup_verify_title_email),
        countDown = countDown,
        canSend = canSend,
        codeValid = valid,
        code = code,
        onCodeChange = { viewModel.setCode(it) },
        onSendCode = {
            scope.launch {
                viewModel.sendCodeNow(email)
            }
        },
        onVerify = {
            scope.launch {
                viewModel.verifyCodeNow(code, email, skipValidate = true)
                    .onSuccess { target ->
                        navController.navigate(
                            SettingRoute.BackupData.BackupData_BackupMerge(
                                "email",
                                email,
                                code,
                                download_url = target.download_url,
                                size = target.size,
                                uploaded_at = target.uploaded_at,
                                abstract = target.abstract,
                            ),
                        )
                    }
                    .onFailure {
                        navController.navigate(
                            SettingRoute.BackupData.BackupData_BackupCloud(
                                "email",
                                email,
                                code
                            )
                        )
                    }
            }
        },
        subTitle = {
            Text(text = stringResource(R.string.scene_backup_backup_verify_field_email))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = email, color = MaterialTheme.colors.primary)
        },
        footer = {
            if (phone != null) {
                Spacer(modifier = Modifier.height(20.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(SettingRoute.BackupData.BackupSelection_Phone(phone)) {
                            popUpTo(SettingRoute.BackupData.BackupSelection_Email.path) {
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text(text = stringResource(R.string.scene_backup_with_phone))
                }
            }
        }
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupSelection_Phone.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun BackupSelectionPhone(
    navController: NavController,
    @Path("phone") phone: String,
) {
    val scope = rememberCoroutineScope()

    val repository = get<PersonaServices>()
    val persona by repository.currentPersona.observeAsState(initial = null)
    val email = persona?.email

    val viewModel = getViewModel<PhoneBackupViewModel>()
    val code by viewModel.code.observeAsState()
    val canSend by viewModel.canSend.observeAsState()
    val valid by viewModel.codeValid.observeAsState()
    val countDown by viewModel.countdown.observeAsState()
    val loading by viewModel.loading.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.startCountDown()
        viewModel.sendCodeNow(phone)
    }

    PhoneCodeInputModal(
        phone = phone,
        code = code,
        onCodeChange = { viewModel.setCode(it) },
        canSend = canSend,
        codeValid = valid,
        countDown = countDown,
        buttonEnabled = loading,
        onSendCode = {
            scope.launch {
                viewModel.sendCodeNow(phone)
            }
        },
        onVerify = {
            scope.launch {
                viewModel.verifyCodeNow(code = code, phone = phone, skipValidate = true)
                    .onSuccess { target ->
                        navController.navigate(
                            SettingRoute.BackupData.BackupData_BackupMerge(
                                "phone",
                                phone,
                                code,
                                download_url = target.download_url,
                                size = target.size,
                                uploaded_at = target.uploaded_at,
                                abstract = target.abstract
                            ),
                        )
                    }
                    .onFailure {
                        navController.navigate(
                            SettingRoute.BackupData.BackupData_BackupCloud(
                                "phone",
                                phone,
                                code
                            )
                        )
                    }
            }
        },
        title = stringResource(R.string.scene_backup_backup_verify_title_phone),
        subTitle = {
            Text(text = stringResource(R.string.scene_backup_backup_verify_field_phone))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = phone, color = MaterialTheme.colors.primary)
        },
        footer = {
            if (email != null) {
                Spacer(modifier = Modifier.height(20.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(SettingRoute.BackupData.BackupSelection_Email(email)) {
                            popUpTo(SettingRoute.BackupData.BackupSelection_Phone.path) {
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text(text = stringResource(R.string.scene_backup_with_email))
                }
            }
        }
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupSelection,
    deeplink = [
        Deeplinks.Setting.BackupData.BackupSelection
    ],
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun BackupSelection(
    navController: NavController,
) {
    val repository = get<PersonaServices>()
    val persona by repository.currentPersona.observeAsState(initial = null)
    BackupSelectionModal(
        onLocal = {
            navController.navigate(SettingRoute.BackupData.BackupLocalHost)
        },
        onRemote = {
            val email = persona?.email
            val phone = persona?.phone
            if (email == null && phone == null) {
                navController.navigate(SettingRoute.BackupData.BackupSelection_NoEmailAndPhone)
            } else if (email != null) {
                navController.navigate(SettingRoute.BackupData.BackupSelection_Email(email))
            } else if (phone != null) {
                navController.navigate(SettingRoute.BackupData.BackupSelection_Phone(phone))
            }
        }
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.Password,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun BackupDataPassword(
    navController: NavController,
) {
    val repository = get<ISettingsRepository>()
    val currentPassword by repository.backupPassword.observeAsState(initial = "")
    var password by remember { mutableStateOf("") }
    BackupPasswordInputModal(
        password = password,
        onPasswordChanged = {
            password = it
        },
        onNext = {
            navController.navigate(SettingRoute.BackupData.BackupLocalHost) {
                popUpTo(SettingRoute.BackupData.Password) {
                    inclusive = true
                }
            }
        },
        enabled = currentPassword == password
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupLocalHost,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun BackupDataBackupLocalHost(
    navController: NavController,
) {
    BackupLocalHost(
        onBack = {
            navController.popBackStack()
        },
        onFailure = {
            navController.navigate(SettingRoute.BackupData.BackupLocalFailure) {
                popUpTo(SettingRoute.BackupData.BackupLocalHost) {
                    inclusive = true
                }
            }
        },
        onSuccess = {
            navController.navigate(SettingRoute.BackupData.BackupLocalSuccess) {
                popUpTo(SettingRoute.BackupData.BackupLocalHost) {
                    inclusive = true
                }
            }
        }
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupLocalFailure,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun BackupDataBackupLocalFailure(
    navController: NavController,
) {
    MaskDialog(
        onDismissRequest = { navController.popBackStack() },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_failed),
                contentDescription = null
            )
        },
        title = { Text(text = stringResource(R.string.common_alert_local_backup_backup_failed)) },
    )
}

@NavGraphDestination(
    route = SettingRoute.BackupData.BackupLocalSuccess,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun BackupDataBackupLocalSuccess(
    navController: NavController,
) {
    MaskDialog(
        onDismissRequest = { navController.popBackStack() },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_snccess),
                contentDescription = null
            )
        },
        title = { Text(text = stringResource(R.string.common_alert_local_backup_backup_completed)) },
    )
}
