package com.dimension.maskbook.wallet.ui.scenes.settings

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.*
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ui.widget.*
import org.koin.androidx.compose.get

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
                icon = R.drawable.en1,
                trailingText = languageMap[language],
            )
            SettingsItem(
                targetRoute = "AppearanceSettings",
                title = "Appearance",
                icon = R.drawable.star1,
                trailingText = appearanceMap[appearance],
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
                    title = "Payment Password",
                    icon = R.drawable.ic_change_payment_password,
                    secondaryText = "Please set up payment password"
                )
            } else {
                SettingsItem(
                    targetRoute = "PaymentPasswordSettings",
                    title = "Change Payment Password",
                    icon = R.drawable.ic_change_payment_password,
                )
            }
            SettingsItem(
                title = "Unlock wallet with Face ID",
                icon = R.drawable.face_id,
                trailing = {
                    Switch(checked = biometricEnabled, onCheckedChange = {})
                },
                onClicked = {
                    repository.setBiometricEnabled(!biometricEnabled)
                },
            )
            MaskListItem {
                Text(text = "Backup & Recovery")
            }
            SettingsItem(
                title = "Back Up Data",
                icon = R.drawable.folder1,
                targetRoute = if (backupPassword.isEmpty() || paymentPassword.isEmpty()) "SetupPasswordDialog" else "BackupData"
            )
            SettingsItem(
                title = "Restore Data",
                icon = R.drawable.ic_restore_data,
                targetRoute = "Recovery"
            )
            if (backupPassword.isEmpty()) {
                SettingsItem(
                    title = "Backup Password",
                    icon = R.drawable.lock1,
                    targetRoute = "ChangeBackUpPassword",
                    secondaryText = "Please set up backup password"
                )
            } else {
                SettingsItem(
                    title = "Change Backup Password",
                    icon = R.drawable.lock1,
                    targetRoute = "ChangeBackUpPassword"
                )
            }
            val email = persona?.email
            if (email == null) {
                SettingsItem(
                    title = "Email",
                    icon = R.drawable.message1,
                    secondaryText = "Please bind your email",
                    targetRoute = "Settings_ChangeEmail_Setup"
                )
            } else {
                SettingsItem(
                    title = "Email",
                    icon = R.drawable.message1,
                    secondaryText = email,
                    targetRoute = "Settings_ChangeEmail_Change_Code/${email.encodeUrl()}"
                )
            }
            val phone = persona?.phone
            if (phone == null) {
                SettingsItem(
                    title = "Phone Number",
                    icon = R.drawable.phone_number,
                    secondaryText = "Please bind your phone number",
                    targetRoute = "Settings_ChangePhone_Setup"
                )
            } else {
                SettingsItem(
                    title = "Phone Number",
                    icon = R.drawable.phone_number,
                    secondaryText = phone,
                    targetRoute = "Settings_ChangePhone_Change_Code/${phone.encodeUrl()}"
                )
            }
        }
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