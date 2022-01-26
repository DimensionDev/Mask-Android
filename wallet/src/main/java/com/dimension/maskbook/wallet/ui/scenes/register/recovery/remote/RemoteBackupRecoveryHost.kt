package com.dimension.maskbook.wallet.ui.scenes.register.recovery.remote

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.route.Root
import com.dimension.maskbook.wallet.ui.RouteType
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.register.EmailRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.PhoneRemoteBackupRecoveryViewModel
import com.dimension.maskbook.wallet.viewmodel.register.RemoteBackupRecoveryViewModelBase
import moe.tlaster.kroute.processor.Back
import moe.tlaster.kroute.processor.Path
import moe.tlaster.kroute.processor.RouteGraphDestination
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf


@RouteGraphDestination(
    route = Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_NoBackup,
    packageName = RouteType.Dialog.PackageName,
    functionName = RouteType.Dialog.FunctionName,
)
@Composable
fun RemoteBackupRecovery_NoBackupRoute(
    @Back onBack: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = { onBack.invoke() },
        title = { Text(text = "No Backup data found!") },
    )
}

@RouteGraphDestination(
    route = Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email_Code.path,
    packageName = RouteType.Modal.PackageName,
    functionName = RouteType.Modal.FunctionName,
)
@Composable
fun RemoteBackupRecovery_Email_CodeRoute(
    @Path("email") email: String?,
    navController: NavController,
) {
    if (email != null) {
        val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
            when (it.target) {
                RemoteBackupRecoveryViewModelBase.NavigateTarget.NoBackup -> navController.navigate(
                    Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_NoBackup
                )
                RemoteBackupRecoveryViewModelBase.NavigateTarget.RestoreBackup -> navController.navigate(
                    Root.Register.Recovery.LocalBackup.RemoteBackupRecovery_RecoveryLocal(it.value)
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
            onVerify = { viewModel.verifyCode(code, email, skipValidate = true) },
            title = stringResource(R.string.scene_restore_titles_recovery_with_email)
        )
    }
}

@RouteGraphDestination(
    route = Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email,
    packageName = RouteType.Modal.PackageName,
    functionName = RouteType.Modal.FunctionName,
)
@Composable
fun RemoteBackupRecovery_EmailRoute(
    navController: NavController,
) {
    val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
        navController.navigate(Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email_Code(it.value))
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
                text = stringResource(R.string.scene_restore_titles_recovery_with_email),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6,
            )
            Spacer(modifier = Modifier.height(21.dp))
            Text(text = stringResource(R.string.scene_backup_backup_verify_field_email))
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
                    navController.navigate(Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone) {
                        popUpTo(Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email) {
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


@RouteGraphDestination(
    route = Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone_Code.path,
    packageName = RouteType.Modal.PackageName,
    functionName = RouteType.Modal.FunctionName,
)
@Composable
fun RemoteBackupRecovery_Phone_CodeRoute(
    @Path("phone") phone: String?,
    navController: NavController
) {
    if (phone != null) {
        val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
            when (it.target) {
                RemoteBackupRecoveryViewModelBase.NavigateTarget.NoBackup -> navController.navigate(
                    Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_NoBackup
                )
                RemoteBackupRecoveryViewModelBase.NavigateTarget.RestoreBackup -> navController.navigate(
                    Root.Register.Recovery.LocalBackup.RemoteBackupRecovery_RecoveryLocal(it.value)
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
                    text = stringResource(R.string.scene_restore_titles_recovery_with_mobile),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                )
                Spacer(modifier = Modifier.height(21.dp))
                Text(text = stringResource(R.string.scene_backup_validation_code))
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
}

@RouteGraphDestination(
    route = Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone,
    packageName = RouteType.Modal.PackageName,
    functionName = RouteType.Modal.FunctionName,
)
@Composable
fun RemoteBackupRecovery_PhoneRoute(
    navController: NavController,
) {
    val requestNavigate: (RemoteBackupRecoveryViewModelBase.NavigateArgs) -> Unit = {
        navController.navigate(Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone_Code(it.value))
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
                text = stringResource(R.string.scene_restore_titles_recovery_with_mobile),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6,
            )
            Spacer(modifier = Modifier.height(21.dp))
            Text(text = stringResource(R.string.scene_backup_backup_verify_field_phone))
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
                    navController.navigate(Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email) {
                        popUpTo(Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Phone) {
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
