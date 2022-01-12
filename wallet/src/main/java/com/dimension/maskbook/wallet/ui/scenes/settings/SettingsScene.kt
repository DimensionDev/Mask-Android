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
                Text(text = "General")
            }
            SettingsItem(
                targetRoute = "LanguageSettings",
                title = "Language",
                icon = R.drawable.ic_settings_language,
                trailingText = languageMap[language],
            )
            SettingsItem(
                targetRoute = "AppearanceSettings",
                title = "Appearance",
                icon = R.drawable.ic_settings_appearance,
                trailingText = appearanceMap[appearance],
            )
            SettingsItem(
                targetRoute = "DataSourceSettings",
                title = "DataSource",
                icon = R.drawable.ic_settings_datasource,
                trailingText = dataProviderMap[dataProvider],
            )
            if (paymentPassword.isEmpty()) {
                SettingsItem(
                    targetRoute = "PaymentPasswordSettings",
                    title = "Payment Password",
                    icon = R.drawable.ic_settings_change_payment_password,
                    secondaryText = "Please set up payment password"
                )
            } else {
                SettingsItem(
                    targetRoute = "PaymentPasswordSettings",
                    title = "Change Payment Password",
                    icon = R.drawable.ic_settings_change_payment_password,
                )
            }
            if (biometricEnableViewModel.isSupported(context)) {
                SettingsItem(
                    title = "Unlock wallet with Face ID",
                    icon = R.drawable.ic_settings_face_id,
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
                Text(text = "Backup & Recovery")
            }
            SettingsItem(
                title = "Back Up Data",
                icon = R.drawable.ic_settings_backup_data,
                targetRoute = if (backupPassword.isEmpty() || paymentPassword.isEmpty()) "SetupPasswordDialog" else "BackupData"
            )
            SettingsItem(
                title = "Restore Data",
                icon = R.drawable.ic_settings_restore_data,
                targetRoute = "Recovery"
            )
            if (backupPassword.isEmpty()) {
                SettingsItem(
                    title = "Backup Password",
                    icon = R.drawable.ic_settings_backup_password,
                    targetRoute = "ChangeBackUpPassword",
                    secondaryText = "Please set up backup password"
                )
            } else {
                SettingsItem(
                    title = "Change Backup Password",
                    icon = R.drawable.ic_settings_backup_password,
                    targetRoute = "ChangeBackUpPassword"
                )
            }
            val email = persona?.email
            if (email == null) {
                SettingsItem(
                    title = "Email",
                    icon = R.drawable.ic_settings_email,
                    secondaryText = "Please bind your email",
                    targetRoute = "Settings_ChangeEmail_Setup"
                )
            } else {
                SettingsItem(
                    title = "Email",
                    icon = R.drawable.ic_settings_email,
                    secondaryText = email,
                    targetRoute = "Settings_ChangeEmail_Change_Code/${email.encodeUrl()}"
                )
            }
            val phone = persona?.phone
            if (phone == null) {
                SettingsItem(
                    title = "Phone Number",
                    icon = R.drawable.ic_settings_phone_number,
                    secondaryText = "Please bind your phone number",
                    targetRoute = "Settings_ChangePhone_Setup"
                )
            } else {
                SettingsItem(
                    title = "Phone Number",
                    icon = R.drawable.ic_settings_phone_number,
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
            title = "Unlock wallet with Face ID",
            negativeButton = "Cancel",
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