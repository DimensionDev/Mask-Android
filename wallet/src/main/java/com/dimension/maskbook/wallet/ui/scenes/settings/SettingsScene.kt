package com.dimension.maskbook.wallet.ui.scenes.settings

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.*
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.wallets.BiometricEnableViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScene(
    onBack: () -> Unit,
) {
    val repository = get<ISettingsRepository>()
    val language by repository.language.observeAsState(initial = Language.auto)
    val appearance by repository.appearance.observeAsState(initial = Appearance.default)
    val dataProvider by repository.dataProvider.observeAsState(initial = DataProvider.UNISWAP_INFO)
    val backupPassword by repository.backupPassword.observeAsState(initial = "")
    val paymentPassword by repository.paymentPassword.observeAsState(initial = "")
    val biometricEnabled by repository.biometricEnabled.observeAsState(initial = false)
    val personaRepository = get<IPersonaRepository>()
    val persona by personaRepository.currentPersona.observeAsState(initial = null)
    val biometricEnableViewModel = getViewModel<BiometricEnableViewModel>()
    val context = LocalContext.current
    MaskScaffold(
        topBar = {
            MaskTopAppBar(
                actions = {
                    MaskCard(
                        modifier = Modifier.aspectRatio(1f)
                    ) {
                        IconButton(onClick = { onBack.invoke() }) {
                            Image(
                                painter = painterResource(id = R.drawable.twitter_1),
                                contentDescription = null,
                            )
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            MaskListItem {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_general_title))
            }
            SettingsItem(
                targetRoute = "LanguageSettings",
                title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_general_language),
                icon = R.drawable.en1,
                trailingText = languageMap[language],
            )
            SettingsItem(
                targetRoute = "AppearanceSettings",
                title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_general_appearance),
                icon = R.drawable.star1,
                trailingText = appearanceMap[appearance]?.let { it1 -> stringResource(it1) },
            )
            SettingsItem(
                targetRoute = "DataSourceSettings",
                title = "DataSource",
                icon = R.drawable.chart_1,
                trailingText = dataProviderMap[dataProvider],
            )
            if (paymentPassword.isEmpty()) {
                SettingsItem(
                    targetRoute = "PaymentPasswordSettings",
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_general_setup_payment_password),
                    icon = R.drawable.ic_change_payment_password,
                    secondaryText = stringResource(R.string.scene_set_password_title)
                )
            } else {
                SettingsItem(
                    targetRoute = "PaymentPasswordSettings",
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_general_change_payment_password),
                    icon = R.drawable.ic_change_payment_password,
                )
            }
            if (biometricEnableViewModel.isSupported(context)) {
                SettingsItem(
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_general_unlock_wallet_with_face_id),
                    icon = R.drawable.face_id,
                    trailing = {
                        Switch(checked = biometricEnabled, onCheckedChange = {
                            enableBiometric(
                                !biometricEnabled,
                                context,
                                biometricEnableViewModel,
                                repository
                            )
                        })
                    },
                    onClicked = {
                        enableBiometric(
                            !biometricEnabled,
                            context,
                            biometricEnableViewModel,
                            repository
                        )
                    },
                )
            }
            MaskListItem {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_backup_recovery_title))
            }
            SettingsItem(
                title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_backup_recovery_back_up_data),
                icon = R.drawable.folder1,
                targetRoute = if (backupPassword.isEmpty() || paymentPassword.isEmpty()) "SetupPasswordDialog" else "BackupData"
            )
            SettingsItem(
                title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_backup_recovery_restore_data),
                icon = R.drawable.ic_restore_data,
                targetRoute = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_personas_action_recovery)
            )
            if (backupPassword.isEmpty()) {
                SettingsItem(
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_backup_recovery_back_up_password),
                    icon = R.drawable.lock1,
                    targetRoute = "ChangeBackUpPassword",
                    secondaryText = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_backup_recovery_back_up_password_empty)
                )
            } else {
                SettingsItem(
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_backup_recovery_change_backup_password),
                    icon = R.drawable.lock1,
                    targetRoute = "ChangeBackUpPassword"
                )
            }
            val email = persona?.email
            if (email == null) {
                SettingsItem(
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_backup_backup_verify_field_email),
                    icon = R.drawable.message1,
                    secondaryText = stringResource(R.string.scene_setting_profile_email_empty),
                    targetRoute = "Settings_ChangeEmail_Setup"
                )
            } else {
                SettingsItem(
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_backup_backup_verify_field_email),
                    icon = R.drawable.message1,
                    secondaryText = email,
                    targetRoute = "Settings_ChangeEmail_Change_Code/${email.encodeUrl()}"
                )
            }
            val phone = persona?.phone
            if (phone == null) {
                SettingsItem(
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_profile_phone_number),
                    icon = R.drawable.phone_number,
                    secondaryText = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_profile_phone_number_empty),
                    targetRoute = "Settings_ChangePhone_Setup"
                )
            } else {
                SettingsItem(
                    title = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_profile_phone_number),
                    icon = R.drawable.phone_number,
                    secondaryText = phone,
                    targetRoute = "Settings_ChangePhone_Change_Code/${phone.encodeUrl()}"
                )
            }
        }
    }
}

private fun enableBiometric(
    enable: Boolean,
    context: Context,
    viewModel: BiometricEnableViewModel,
    repository: ISettingsRepository
) {
    if (enable) {
        viewModel.enable(
            context = context,
            title = com.dimension.maskbook.wallet.R.string.scene_setting_general_unlock_wallet_with_face_id,
            negativeButton = com.dimension.maskbook.wallet.R.string.common_controls_cancel,
        )
    } else {
        repository.setBiometricEnabled(enable)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsItem(
    targetRoute: String? = null,
    title: String,
    @DrawableRes icon: Int,
    secondaryText: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    trailingText: String? = null,
    onClicked: (() -> Unit)? = null,
) {
    val rootNavController = LocalRootNavController.current
    MaskListCardItem(
        modifier = Modifier
            .clickable {
                if (targetRoute != null) {
                    rootNavController.navigate(targetRoute)
                } else onClicked?.invoke()
            },
        text = {
            Text(text = title)
        },
        icon = {
            Image(painterResource(id = icon), contentDescription = null)
        },
        trailing = {
            if (trailing != null) {
                trailing.invoke()
            } else {
                Row(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (trailingText != null) {
                        Text(text = trailingText)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        },
        secondaryText = secondaryText?.let {
            {
                Text(text = it)
            }
        }
    )
}