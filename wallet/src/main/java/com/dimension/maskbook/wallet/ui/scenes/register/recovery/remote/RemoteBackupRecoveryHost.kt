package com.dimension.maskbook.wallet.ui.scenes.register.recovery.remote

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.widget.EmailCodeInputModal
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
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
    dialog("RemoteBackupRecovery_NoBackup") {
        MaskDialog(
            onDismissRequest = { navController.popBackStack() },
            title = { Text(text = "No Backup data found!") })
    }
    bottomSheet(
        "RemoteBackupRecovery_Email_Code/{email}",
        arguments = listOf(navArgument("email") { type = NavType.StringType })
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("email")?.let { email ->
            val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                when (it.target) {
                    RemoteBackupRecoveryViewModelBase.NavigateTarget.NoBackup -> navController.navigate(
                        "RemoteBackupRecovery_NoBackup"
                    )
                    RemoteBackupRecoveryViewModelBase.NavigateTarget.RestoreBackup -> navController.navigate(
                        "RemoteBackupRecovery_RecoveryLocal/${it.value.encodeUrl()}"
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
                onVerify = { viewModel.verifyCode(code, email) },
                title = "Recovery with Email"
            )
        } ?: run {
            navController.popBackStack()
        }
    }
    bottomSheet("RemoteBackupRecovery_Email") {
        val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
            navController.navigate("RemoteBackupRecovery_Email_Code/${it.value.encodeUrl()}")
        }
        val viewModel = getViewModel<EmailRemoteBackupRecoveryViewModel> {
            parametersOf(requestNavigate)
        }
        val email by viewModel.value.observeAsState(initial = "")
        val emailValid by viewModel.valueValid.observeAsState(initial = true)
        val loading by viewModel.loading.observeAsState(initial = false)
        MaskModal {
            Column(
                modifier = Modifier.padding(ScaffoldPadding)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Recovery with Email",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                )
                Spacer(modifier = Modifier.height(21.dp))
                Text(text = "Email")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
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
                    Text(text = "Incorrect Email", color = Color.Red)
                }
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.sendCode(email)
                    },
                    enabled = emailValid && !loading && email.isNotEmpty(),
                ) {
                    Text(text = "Next")
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("RemoteBackupRecovery_Phone") {
                            popUpTo("RemoteBackupRecovery_Email") {
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text(text = "Recovery with Mobile")
                }
            }
        }
    }
    bottomSheet(
        "RemoteBackupRecovery_Phone_Code/{phone}",
        arguments = listOf(navArgument("phone") { type = NavType.StringType })
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("phone")?.let { phone ->
                val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
                    when (it.target) {
                        RemoteBackupRecoveryViewModelBase.NavigateTarget.NoBackup -> navController.navigate(
                            "RemoteBackupRecovery_NoBackup"
                        )
                        RemoteBackupRecoveryViewModelBase.NavigateTarget.RestoreBackup -> navController.navigate(
                            "RemoteBackupRecovery_RecoveryLocal/${it.value.encodeUrl()}"
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
                MaskModal {
                    Column(
                        modifier = Modifier.padding(ScaffoldPadding)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Recovery with Phone Number",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h6,
                        )
                        Spacer(modifier = Modifier.height(21.dp))
                        Text(text = "Verification Code")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
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
                                    Text(text = "Resend")
                                } else {
                                    Text(text = countDown.toString() + "s")
                                }
                            }
                        }
                        if (!codeValid) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Incorrect Code", color = Color.Red)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Verification code has been sent to by SMS to $phone"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.verifyCode(code, phone)
                            },
                            enabled = code.isNotEmpty() && !loading,
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                }
            } ?: run {
            navController.popBackStack()
        }
    }
    bottomSheet("RemoteBackupRecovery_Phone") {
        val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
            val a = it.value.encodeUrl()
            navController.navigate("RemoteBackupRecovery_Phone_Code/${it.value.encodeUrl()}")
        }
        val viewModel = getViewModel<PhoneRemoteBackupRecoveryViewModel> {
            parametersOf(requestNavigate)
        }
        val regionCode by viewModel.regionCode.observeAsState(initial = "+86")
        val phone by viewModel.value.observeAsState(initial = "")
        val phoneValid by viewModel.valueValid.observeAsState(initial = true)
        val loading by viewModel.loading.observeAsState(initial = false)
        MaskModal {
            Column(
                modifier = Modifier.padding(ScaffoldPadding)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Recovery with Phone Number",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                )
                Spacer(modifier = Modifier.height(21.dp))
                Text(text = "Phone")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = regionCode,
                        onValueChange = { viewModel.setRegionCode(it) },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
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
                    Text(text = "Incorrect Phone Number", color = Color.Red)
                }
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.sendCode(regionCode + phone)
                    },
                    enabled = phoneValid && !loading && phone.isNotEmpty(),
                ) {
                    Text(text = "Next")
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("RemoteBackupRecovery_Email") {
                            popUpTo("RemoteBackupRecovery_Phone") {
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text(text = "Recovery with Email")
                }
            }
        }
    }
}