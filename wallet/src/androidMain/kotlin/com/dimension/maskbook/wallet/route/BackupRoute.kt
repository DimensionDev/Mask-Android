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
package com.dimension.maskbook.wallet.route

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.services.model.DownloadResponse
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.local.BackupPasswordInputModal
import com.dimension.maskbook.wallet.ui.scenes.settings.BackupLocalHost
import com.dimension.maskbook.wallet.ui.scenes.settings.backup.BackupCloudScene
import com.dimension.maskbook.wallet.ui.scenes.settings.backup.BackupSelectionModal
import com.dimension.maskbook.wallet.ui.widget.EmailCodeInputModal
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.MaskInputField
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.PhoneCodeInputModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton
import com.dimension.maskbook.wallet.viewmodel.settings.BackupCloudExecuteViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.BackupMergeConfirmViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.EmailBackupViewModel
import com.dimension.maskbook.wallet.viewmodel.settings.PhoneBackupViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@ExperimentalAnimationApi
@ExperimentalMaterialNavigationApi
fun NavGraphBuilder.backupRoute(
    navController: NavController,
) {
    navigation("BackupSelection", "BackupData") {
        dialog("BackupData_Cloud_Success") {
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
        dialog("BackupData_Cloud_Failed") {
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
        composable(
            "BackupData_BackupCloud/{type}/{value}/{code}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("value") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@composable
            val value = backStackEntry.arguments?.getString("value") ?: return@composable
            val code = backStackEntry.arguments?.getString("code") ?: return@composable
            BackupCloudScene(
                onBack = {
                    navController.popBackStack()
                },
                onConfirm = {
                    navController.navigate(
                        "BackupData_BackupCloud_Execute/$it/$type/$value/$code",
                        navOptions = navOptions {
                            popUpTo(backStackEntry.destination.id) {
                                inclusive = true
                            }
                        }
                    )
                }
            )
        }
        composable(
            "BackupData_BackupCloud_Execute/{withWallet}/{type}/{value}/{code}",
            arguments = listOf(
                navArgument("withWallet") { type = NavType.BoolType },
                navArgument("type") { type = NavType.StringType },
                navArgument("value") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@composable
            val value = backStackEntry.arguments?.getString("value") ?: return@composable
            val code = backStackEntry.arguments?.getString("code") ?: return@composable
            backStackEntry.arguments?.getBoolean("withWallet")?.let { withWallet ->
                val viewModel = getViewModel<BackupCloudExecuteViewModel>()
                LaunchedEffect(Unit) {
                    val result = viewModel.execute(
                        code = code,
                        type = type,
                        account = value,
                        withWallet = withWallet,
                    )
                    if (result) {
                        navController.navigate("BackupData_Cloud_Success") {
                            popUpTo(backStackEntry.destination.id) {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate("BackupData_Cloud_Failed") {
                            popUpTo(backStackEntry.destination.id) {
                                inclusive = true
                            }
                        }
                    }
                }
                MaskTheme {
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
        }
        dialog("BackupSelection_NoEmailAndPhone") {
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
        dialog(
            "BackupData_BackupMerge_Confirm_Success/{type}/{value}/{code}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("value") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@dialog
            val value = backStackEntry.arguments?.getString("value") ?: return@dialog
            val code = backStackEntry.arguments?.getString("code") ?: return@dialog
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
                                navController.popBackStack("BackupSelection", inclusive = true)
                            }
                        ) {
                            Text(text = stringResource(R.string.common_controls_cancel))
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        PrimaryButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate("BackupData_BackupCloud/$type/$value/$code") {
                                    popUpTo("BackupSelection") {
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
        bottomSheet(
            "BackupData_BackupMerge_Confirm/{type}/{value}/{code}?download_url={download_url}&size={size}&uploaded_at={uploaded_at}&abstract={abstract}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("value") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType },
                navArgument("download_url") { type = NavType.StringType; nullable = true },
                navArgument("size") { type = NavType.LongType; defaultValue = 0L },
                navArgument("uploaded_at") { type = NavType.LongType; defaultValue = 0L },
                navArgument("abstract") { type = NavType.StringType; nullable = true },
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@bottomSheet
            val value = backStackEntry.arguments?.getString("value") ?: return@bottomSheet
            val code = backStackEntry.arguments?.getString("code") ?: return@bottomSheet
            val download_url =
                backStackEntry.arguments?.getString("download_url") ?: return@bottomSheet
            val size = backStackEntry.arguments?.getLong("size") ?: return@bottomSheet
            val uploaded_at = backStackEntry.arguments?.getLong("uploaded_at") ?: return@bottomSheet
            val abstract = backStackEntry.arguments?.getString("abstract") ?: return@bottomSheet

            val onDone: () -> Unit = {
                navController.navigate("BackupData_BackupMerge_Confirm_Success/$type/$value/$code") {
                    popUpTo("BackupData_BackupMerge/{type}/{value}/{code}?download_url={download_url}&size={size}&uploaded_at={uploaded_at}&abstract={abstract}") {
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
                Column(
                    modifier = Modifier.padding(ScaffoldPadding)
                ) {
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
                            Text(text = abstract)
                            Text(text = uploaded_at.toString())
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
                            viewModel.confirm(type, download_url)
                        },
                        enabled = passwordValid && !loading
                    ) {
                        Text(text = stringResource(R.string.common_controls_merge_to_local_data))
                    }
                }
            }
        }
        bottomSheet(
            "BackupData_BackupMerge/{type}/{value}/{code}?download_url={download_url}&size={size}&uploaded_at={uploaded_at}&abstract={abstract}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("value") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType },
                navArgument("download_url") { type = NavType.StringType; nullable = true },
                navArgument("size") { type = NavType.LongType; defaultValue = 0L },
                navArgument("uploaded_at") { type = NavType.LongType; defaultValue = 0L },
                navArgument("abstract") { type = NavType.StringType; nullable = true },
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@bottomSheet
            val value = backStackEntry.arguments?.getString("value") ?: return@bottomSheet
            val code = backStackEntry.arguments?.getString("code") ?: return@bottomSheet
            val download_url =
                backStackEntry.arguments?.getString("download_url") ?: return@bottomSheet
            val size = backStackEntry.arguments?.getLong("size") ?: return@bottomSheet
            val uploaded_at = backStackEntry.arguments?.getLong("uploaded_at") ?: return@bottomSheet
            val abstract = backStackEntry.arguments?.getString("abstract") ?: return@bottomSheet
            MaskModal {
                Column(
                    modifier = Modifier.padding(ScaffoldPadding)
                ) {
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
                            Text(text = abstract)
                            Text(text = uploaded_at.toString())
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
                            navController.navigate("BackupData_BackupMerge_Confirm/$type/$value/$code?download_url=$download_url&size=$size&uploaded_at=$uploaded_at&abstract=${abstract.encodeUrl()}")
                        },
                    ) {
                        Text(text = stringResource(R.string.common_controls_merge_and_back_up))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(onClick = {
                        navController.navigate("BackupData_BackupCloud/$type/$value/$code")
                    }) {
                        Text(text = stringResource(R.string.common_controls_back_up))
                    }
                }
            }
        }
        bottomSheet(
            "BackupSelection_Email/{email}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val requestMerge: (target: DownloadResponse, email: String, code: String) -> Unit =
                { target, email, code ->
                    navController.navigate("BackupData_BackupMerge/email/$email/$code?download_url=${target.download_url}&size=${target.size}&uploaded_at=${target.uploaded_at}&abstract=${target.abstract?.encodeUrl()}")
                }
            val next: (email: String, code: String) -> Unit = { email, code ->
                navController.navigate("BackupData_BackupCloud/email/$email/$code")
            }
            backStackEntry.arguments?.getString("email")?.let { email ->
                val repository = get<IPersonaRepository>()
                val persona by repository.currentPersona.observeAsState(initial = null)
                val phone = persona?.phone
                val viewModel = getViewModel<EmailBackupViewModel> {
                    parametersOf(requestMerge, next)
                }
                val code by viewModel.code.observeAsState(initial = "")
                val valid by viewModel.codeValid.observeAsState(initial = true)
                val loading by viewModel.loading.observeAsState(initial = false)
                val canSend by viewModel.canSend.observeAsState(initial = false)
                val countDown by viewModel.countdown.observeAsState(initial = 60)
                LaunchedEffect(Unit) {
                    viewModel.startCountDown()
                    viewModel.sendCode(email)
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
                    onSendCode = { viewModel.sendCode(email) },
                    onVerify = { viewModel.verifyCode(code, email, skipValidate = true) },
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
                                    navController.navigate("BackupSelection_Phone/$phone") {
                                        popUpTo("BackupSelection_Email") {
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
        }
        bottomSheet(
            "BackupSelection_Phone/{phone}",
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestMerge: (target: DownloadResponse, phone: String, code: String) -> Unit =
                { target, phone, code ->
                    navController.navigate("BackupData_BackupMerge/phone/$phone/$code?download_url=${target.download_url}&size=${target.size}&uploaded_at=${target.uploaded_at}&abstract=${target.abstract?.encodeUrl()}")
                }
            val next: (phone: String, code: String) -> Unit = { phone, code ->
                navController.navigate("BackupData_BackupCloud/phone/$phone/$code")
            }
            backStackEntry.arguments?.getString("phone")?.let { phone ->
                val repository = get<IPersonaRepository>()
                val persona by repository.currentPersona.observeAsState(initial = null)
                val email = persona?.email
                val viewModel = getViewModel<PhoneBackupViewModel> {
                    parametersOf(requestMerge, next)
                }
                val code by viewModel.code.observeAsState(initial = "")
                val canSend by viewModel.canSend.observeAsState(initial = false)
                val valid by viewModel.codeValid.observeAsState(initial = true)
                val countDown by viewModel.countdown.observeAsState(initial = 60)
                val loading by viewModel.loading.observeAsState(initial = false)
                LaunchedEffect(Unit) {
                    viewModel.startCountDown()
                    viewModel.sendCode(phone)
                }
                PhoneCodeInputModal(
                    phone = phone,
                    code = code,
                    onCodeChange = { viewModel.setCode(it) },
                    canSend = canSend,
                    codeValid = valid,
                    countDown = countDown,
                    buttonEnabled = loading,
                    onSendCode = { viewModel.sendCode(phone) },
                    onVerify = { viewModel.verifyCode(code = code, value = phone, skipValidate = true) },
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
                                    navController.navigate("BackupSelection_Email/$email") {
                                        popUpTo("BackupSelection_Phone") {
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
        }
        bottomSheet("BackupSelection") {
            val repository = get<IPersonaRepository>()
            val persona by repository.currentPersona.observeAsState(initial = null)
            BackupSelectionModal(
                onLocal = {
                    navController.navigate("BackupLocalHost")
                },
                onRemote = {
                    val email = persona?.email
                    val phone = persona?.phone
                    if (email == null && phone == null) {
                        navController.navigate("BackupSelection_NoEmailAndPhone")
                    } else if (email != null) {
                        navController.navigate("BackupSelection_Email/$email")
                    } else if (phone != null) {
                        navController.navigate("BackupSelection_Phone/$phone")
                    }
                }
            )
        }
        bottomSheet("Password") {
            val repository = get<ISettingsRepository>()
            val currentPassword by repository.backupPassword.observeAsState(
                initial = ""
            )
            var password by remember {
                mutableStateOf("")
            }

            BackupPasswordInputModal(
                password = password,
                onPasswordChanged = {
                    password = it
                },
                onNext = {
                    navController.navigate("BackupLocalHost") {
                        popUpTo("Password") {
                            inclusive = true
                        }
                    }
                },
                enabled = currentPassword == password
            )
        }
        composable("BackupLocalHost") {
            BackupLocalHost(
                onBack = {
                    navController.popBackStack()
                },
                onFailure = {
                    navController.navigate("BackupLocalFailure") {
                        popUpTo("BackupLocalHost") {
                            inclusive = true
                        }
                    }
                },
                onSuccess = {
                    navController.navigate("BackupLocalSuccess") {
                        popUpTo("BackupLocalHost") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        dialog("BackupLocalFailure") {
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
        dialog("BackupLocalSuccess") {
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
    }
}
