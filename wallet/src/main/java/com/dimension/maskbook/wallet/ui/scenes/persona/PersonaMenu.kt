package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
                            Text(text = "Change or Add Persona")
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
                            Text(text = "Rename")
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
                            Text(text = "Export Private Key")
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
                            Text(text = "Back up")
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
//                        Text(text = "Recovery")
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
                            Text(text = "Log Out", color = Color.Red)
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
//                        Text(text = "Delete")
//                    }
//                }
                }
            }
        }
    }
}
