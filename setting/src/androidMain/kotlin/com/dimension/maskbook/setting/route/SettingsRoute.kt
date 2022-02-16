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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.ui.widget.EmailCodeInputModal
import com.dimension.maskbook.common.ui.widget.EmailInputModal
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.localization.R
import com.dimension.maskbook.setting.ui.scenes.AppearanceSettings
import com.dimension.maskbook.setting.ui.scenes.ChangeBackUpPasswordModal
import com.dimension.maskbook.setting.ui.scenes.ChangePaymentPasswordModal
import com.dimension.maskbook.setting.ui.scenes.DataSourceSettings
import com.dimension.maskbook.setting.ui.scenes.LanguageSettings
import com.dimension.maskbook.setting.ui.scenes.PhoneCodeInputModal
import com.dimension.maskbook.setting.ui.scenes.PhoneInputModal
import com.dimension.maskbook.setting.viewmodel.EmailSetupViewModel
import com.dimension.maskbook.setting.viewmodel.PhoneSetupViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.settingsRoute(
    navController: NavController
) {
    dialog(
        SettingRoute.SetupPasswordDialog,
        deepLinks = listOf(
            navDeepLink { uriPattern = Deeplinks.Setting.SetupPasswordDialog }
        )
    ) {
        MaskDialog(
            onDismissRequest = {
                navController.popBackStack()
            },
            title = {
                Text(text = stringResource(R.string.common_alert_setting_warning_backup_data_titile))
            },
            text = {
                Text(text = stringResource(R.string.common_alert_setting_warning_backup_data_description))
            },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text(text = stringResource(R.string.common_controls_ok))
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_property_1_note),
                    contentDescription = null
                )
            }
        )
    }
    bottomSheet(SettingRoute.LanguageSettings) {
        LanguageSettings(
            onBack = {
                navController.popBackStack()
            }
        )
    }
    bottomSheet(SettingRoute.AppearanceSettings) {
        AppearanceSettings(
            onBack = {
                navController.popBackStack()
            }
        )
    }
    bottomSheet(SettingRoute.DataSourceSettings) {
        DataSourceSettings(
            onBack = {
                navController.popBackStack()
            }
        )
    }
    bottomSheet(SettingRoute.PaymentPasswordSettings) {
        ChangePaymentPasswordModal(
            onConfirm = {
                navController.navigate(SettingRoute.PaymentPasswordSettingsSuccess) {
                    popUpTo(SettingRoute.PaymentPasswordSettings) {
                        inclusive = true
                    }
                }
            }
        )
    }
    dialog(SettingRoute.PaymentPasswordSettingsSuccess) {
        MaskDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Text(text = "Payment Password changed successfully!")
            },
            text = {
                Text(text = stringResource(R.string.common_alert_change_password_description))
            },
            icon = {
                Image(
                    painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.popBackStack() },
                ) {
                    Text(text = stringResource(R.string.common_controls_done))
                }
            }
        )
    }
    bottomSheet(SettingRoute.ChangeBackUpPassword) {
        ChangeBackUpPasswordModal(
            onConfirm = {
                navController.navigate(SettingRoute.ChangeBackUpPasswordSuccess) {
                    popUpTo(SettingRoute.ChangeBackUpPassword) {
                        inclusive = true
                    }
                }
            }
        )
    }
    dialog(SettingRoute.ChangeBackUpPasswordSuccess) {
        MaskDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Text(text = stringResource(R.string.common_alert_change_backup_password_title))
            },
            text = {
                Text(text = stringResource(R.string.common_alert_change_backup_password_description))
            },
            icon = {
                Image(
                    painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.popBackStack() },
                ) {
                    Text(text = stringResource(R.string.common_controls_done))
                }
            }
        )
    }

    bottomSheet(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Setup) {
        val scope = rememberCoroutineScope()

        val viewModel = getViewModel<EmailSetupViewModel>()
        val email by viewModel.value.observeAsState()
        val emailValid by viewModel.valueValid.observeAsState()
        val loading by viewModel.loading.observeAsState()
        EmailInputModal(
            email = email,
            onEmailChange = { viewModel.setValue(it) },
            emailValid = emailValid,
            onConfirm = {
                scope.launch {
                    viewModel.sendCodeNow(email).onSuccess {
                        navController.navigate(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Setup_Code(email))
                    }
                }
            },
            buttonEnabled = loading,
            title = stringResource(R.string.scene_setting_bind_email_title)
        )
    }
    bottomSheet(
        SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Setup_Code.path,
        arguments = listOf(
            navArgument("email") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("email")?.let { email ->
            val scope = rememberCoroutineScope()

            val viewModel = getViewModel<EmailSetupViewModel>()
            val code by viewModel.code.observeAsState()
            val valid by viewModel.codeValid.observeAsState()
            val loading by viewModel.loading.observeAsState()
            val canSend by viewModel.canSend.observeAsState()
            val countDown by viewModel.countdown.observeAsState()

            LaunchedEffect(Unit) {
                viewModel.startCountDown()
            }

            EmailCodeInputModal(
                email = email,
                buttonEnabled = loading,
                title = stringResource(R.string.scene_setting_bind_email_title),
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
                        viewModel.verifyCodeNow(code, email).onSuccess {
                            navController.navigate(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Setup_Success) {
                                popUpTo(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Setup) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            )
        }
    }
    dialog(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Setup_Success) {
        MaskDialog(
            onDismissRequest = { navController.popBackStack() },
            title = { Text(text = stringResource(R.string.scene_setting_bind_remote_info_setup_email_title)) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            text = { Text(text = stringResource(R.string.scene_setting_bind_remote_info_setup_email_detail)) },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.popBackStack() },
                ) {
                    Text(text = stringResource(R.string.common_controls_done))
                }
            }
        )
    }
    bottomSheet(
        SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Change_Code.path,
        arguments = listOf(
            navArgument("email") { type = NavType.StringType }
        ),
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("email")?.let { email ->
            val scope = rememberCoroutineScope()

            val viewModel = getViewModel<EmailSetupViewModel>()
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
                title = stringResource(R.string.scene_setting_change_email_title),
                subTitle = { Text(text = stringResource(R.string.scene_setting_change_email_tips)) },
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
                        viewModel.verifyCodeNow(code, email).onSuccess {
                            navController.navigate(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Change_New)
                        }
                    }
                }
            )
        }
    }
    bottomSheet(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Change_New) {
        val scope = rememberCoroutineScope()

        val viewModel = getViewModel<EmailSetupViewModel>()
        val email by viewModel.value.observeAsState()
        val emailValid by viewModel.valueValid.observeAsState()
        val loading by viewModel.loading.observeAsState()
        EmailInputModal(
            email = email,
            onEmailChange = { viewModel.setValue(it) },
            emailValid = emailValid,
            onConfirm = {
                scope.launch {
                    viewModel.sendCodeNow(email).onSuccess {
                        navController.navigate(
                            SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Change_New_Code(
                                email
                            )
                        )
                    }
                }
            },
            buttonEnabled = loading,
            title = stringResource(R.string.scene_setting_change_email_title)
        )
    }

    bottomSheet(
        SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Change_New_Code.path,
        arguments = listOf(
            navArgument("email") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("email")?.let { email ->
            val scope = rememberCoroutineScope()

            val viewModel = getViewModel<EmailSetupViewModel>()
            val code by viewModel.code.observeAsState()
            val valid by viewModel.codeValid.observeAsState()
            val loading by viewModel.loading.observeAsState()
            val canSend by viewModel.canSend.observeAsState()
            val countDown by viewModel.countdown.observeAsState()

            LaunchedEffect(Unit) {
                viewModel.startCountDown()
            }

            EmailCodeInputModal(
                email = email,
                buttonEnabled = loading,
                title = stringResource(R.string.scene_setting_change_email_title),
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
                        viewModel.verifyCodeNow(code, email).onSuccess {
                            navController.navigate(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Change_Success) {
                                popUpTo(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Change_Code.path) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            )
        }
    }
    dialog(SettingRoute.Settings_ChangeEmail.Settings_ChangeEmail_Change_Success) {
        MaskDialog(
            onDismissRequest = { navController.popBackStack() },
            title = { Text(text = stringResource(R.string.scene_setting_bind_remote_info_change_email_title)) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            text = { Text(text = stringResource(R.string.scene_setting_bind_remote_info_setup_email_detail)) },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.popBackStack() },
                ) {
                    Text(text = stringResource(R.string.common_controls_done))
                }
            }
        )
    }

    bottomSheet(SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Setup) {
        val scope = rememberCoroutineScope()

        val viewModel = getViewModel<PhoneSetupViewModel>()
        val regionCode by viewModel.regionCode.observeAsState()
        val phone by viewModel.value.observeAsState()
        val valid by viewModel.valueValid.observeAsState()
        val loading by viewModel.loading.observeAsState()
        PhoneInputModal(
            regionCode = regionCode,
            onRegionCodeChange = { viewModel.setRegionCode(it) },
            phone = phone,
            onPhoneChange = { viewModel.setValue(it) },
            phoneValid = valid,
            onConfirm = {
                scope.launch {
                    val code = regionCode + phone
                    viewModel.sendCodeNow(code).onSuccess {
                        navController.navigate(
                            SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Setup_Code(code)
                        )
                    }
                }
            },
            buttonEnabled = loading,
            title = stringResource(R.string.scene_setting_bind_phone_number_title),
        )
    }
    bottomSheet(
        SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Setup_Code.path,
        arguments = listOf(navArgument("phone") { type = NavType.StringType })
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("phone")?.let { phone ->
            val scope = rememberCoroutineScope()

            val viewModel = getViewModel<PhoneSetupViewModel>()
            val code by viewModel.code.observeAsState()
            val canSend by viewModel.canSend.observeAsState()
            val valid by viewModel.codeValid.observeAsState()
            val countDown by viewModel.countdown.observeAsState()
            val loading by viewModel.loading.observeAsState()
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
                        viewModel.verifyCodeNow(code = code, phone = phone).onSuccess {
                            navController.navigate(SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Setup_Success) {
                                popUpTo(SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Setup) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                },
                title = stringResource(R.string.scene_setting_bind_phone_number_title)
            )
        }
    }
    dialog(SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Setup_Success) {
        MaskDialog(
            onDismissRequest = { navController.popBackStack() },
            title = { Text(text = stringResource(R.string.scene_setting_bind_remote_info_setup_phone_number_title)) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            text = { Text(text = stringResource(R.string.scene_setting_bind_remote_info_change_email_detail)) },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.popBackStack() },
                ) {
                    Text(text = stringResource(R.string.common_controls_done))
                }
            }
        )
    }

    bottomSheet(
        SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Change_Code.path,
        arguments = listOf(navArgument("phone") { type = NavType.StringType })
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("phone")?.let { phone ->
            val scope = rememberCoroutineScope()

            val viewModel = getViewModel<PhoneSetupViewModel>()
            val code by viewModel.code.observeAsState()
            val canSend by viewModel.canSend.observeAsState()
            val valid by viewModel.codeValid.observeAsState()
            val countDown by viewModel.countdown.observeAsState()
            val loading by viewModel.loading.observeAsState()
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
                        viewModel.verifyCodeNow(code = code, phone = phone).onSuccess {
                            navController.navigate(SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Change_New)
                        }
                    }
                },
                title = stringResource(R.string.scene_setting_change_phone_number_title),
                subTitle = { Text(text = stringResource(R.string.scene_setting_change_phone_number_tips)) }
            )
        }
    }
    bottomSheet(SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Change_New) {
        val scope = rememberCoroutineScope()

        val viewModel = getViewModel<PhoneSetupViewModel>()
        val regionCode by viewModel.regionCode.observeAsState()
        val phone by viewModel.value.observeAsState()
        val valid by viewModel.valueValid.observeAsState()
        val loading by viewModel.loading.observeAsState()
        PhoneInputModal(
            regionCode = regionCode,
            onRegionCodeChange = { viewModel.setRegionCode(it) },
            phone = phone,
            onPhoneChange = { viewModel.setValue(it) },
            phoneValid = valid,
            onConfirm = {
                scope.launch {
                    val code = regionCode + phone
                    viewModel.sendCodeNow(code).onSuccess {
                        navController.navigate(
                            SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Change_New_Code(
                                code
                            )
                        )
                    }
                }
            },
            buttonEnabled = loading,
            title = stringResource(R.string.scene_setting_change_phone_number_title),
        )
    }

    bottomSheet(
        SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Change_New_Code.path,
        arguments = listOf(navArgument("phone") { type = NavType.StringType })
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("phone")?.let { phone ->
            val scope = rememberCoroutineScope()

            val viewModel = getViewModel<PhoneSetupViewModel>()
            val code by viewModel.code.observeAsState()
            val canSend by viewModel.canSend.observeAsState()
            val valid by viewModel.codeValid.observeAsState()
            val countDown by viewModel.countdown.observeAsState()
            val loading by viewModel.loading.observeAsState()
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
                        viewModel.verifyCodeNow(code = code, phone = phone).onSuccess {
                            navController.navigate(SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Change_Success) {
                                popUpTo(SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Change_Code.path) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                },
                title = stringResource(R.string.scene_setting_change_phone_number_title)
            )
        }
    }
    dialog(SettingRoute.Settings_ChangePhone.Settings_ChangePhone_Change_Success) {
        MaskDialog(
            onDismissRequest = { navController.popBackStack() },
            title = { Text(text = stringResource(R.string.scene_setting_bind_remote_info_change_phone_number_title)) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_property_1_snccess),
                    contentDescription = null
                )
            },
            text = { Text(text = stringResource(R.string.scene_setting_bind_remote_info_change_phone_number_detail)) },
            buttons = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.popBackStack() },
                ) {
                    Text(text = stringResource(R.string.common_controls_done))
                }
            }
        )
    }
}
