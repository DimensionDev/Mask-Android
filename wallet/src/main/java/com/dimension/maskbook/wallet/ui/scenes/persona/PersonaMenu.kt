package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.PersonaData
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PersonaMenu(
    personaData: PersonaData,
    navController: NavController,
    onBack: () -> Unit,
) {
    val repository = get<ISettingsRepository>()
    val backupPassword by repository.backupPassword.observeAsState(initial = "")
    val paymentPassword by repository.paymentPassword.observeAsState(initial = "")
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = personaData.name)
                    }
                )
            }
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.subtitle1
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(ScaffoldPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 0.dp,
                        onClick = {
                            navController.navigate("SwitchPersona")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_profile),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.scene_personas_action_change_add_persona))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 0.dp,
                        onClick = {
                            navController.navigate("RenamePersona/${personaData.id.encodeUrl()}")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_edit),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.scene_personas_action_rename))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 0.dp,
                        onClick = {
                            //first check if it has backup password
                            if (backupPassword.isEmpty()) {
                                navController.navigate("SetupPasswordDialog")
                            } else {
                                navController.navigate("BackUpPassword/ExportPrivateKeyScene")
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_password),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.scene_persona_export_private_key_title))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 0.dp,
                        onClick = {
                            navController.navigate(
                                if (backupPassword.isEmpty() || paymentPassword.isEmpty()) "SetupPasswordDialog" else "BackupData"
                            )
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_paper_plus),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.common_controls_back_up))
                        }
                    }
//                Spacer(modifier = Modifier.height(16.dp))
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = 0.dp
//                ) {
//                    Row(
//                        modifier = Modifier.padding(16.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        Image(
//                            painterResource(id = R.drawable.ic_restore),
//                            contentDescription = null
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(text = stringResource(R.string.scene_personas_action_recovery))
//                    }
//                }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 0.dp,
                        onClick = {
                            navController.navigate("Logout")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painterResource(id = R.drawable.ic_logout),
                                contentDescription = null,
                                tint = Color.Red,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.scene_setting_profile_log_out), color = Color.Red)
                        }
                    }
//                Spacer(modifier = Modifier.height(16.dp))
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = 0.dp,
//                    onClick = {
//                        navController.navigate("Delete")
//                    }
//                ) {
//                    Row(
//                        modifier = Modifier.padding(16.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        Image(
//                            painterResource(id = R.drawable.ic_delete),
//                            contentDescription = null
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(text = stringResource(R.string.scene_personas_action_delete))
//                    }
//                }
                }
            }
        }
    }
}
