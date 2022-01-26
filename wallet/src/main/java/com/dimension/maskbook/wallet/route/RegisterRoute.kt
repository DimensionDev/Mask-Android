package com.dimension.maskbook.wallet.route

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.ui.RouteType
import com.dimension.maskbook.wallet.ui.scenes.register.CreatePersonaModal
import com.dimension.maskbook.wallet.ui.scenes.register.CreatePersonaScene
import com.dimension.maskbook.wallet.ui.scenes.register.RegisterScene
import com.dimension.maskbook.wallet.ui.scenes.register.createidentity.CreateIdentityHost
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.IdentityScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.PrivateKeyScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryComplectedScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryHomeScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.local.RecoveryLocalHost
import com.dimension.maskbook.wallet.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.PrivateKeyViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import moe.tlaster.kroute.processor.Back
import moe.tlaster.kroute.processor.Path
import moe.tlaster.kroute.processor.RouteGraphDestination
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@RouteGraphDestination(
    route = Root.Register.Init,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun RegisterRoute(
    navController: NavController,
) {
    val repository = get<IPersonaRepository>()
    val persona by repository.currentPersona.observeAsState(initial = null)
    LaunchedEffect(Unit) {
        snapshotFlow { persona }
            .distinctUntilChanged()
            .collect {
                if (it != null) {
                    navController.navigate(Root.Main.Home) {
                        popUpTo(Root.Register.Init) {
                            inclusive = true
                        }
                    }
                }
            }
    }
    RegisterScene(
        onCreateIdentity = {
            navController.navigate(Root.Register.WelcomeCreatePersona)
        },
        onRecoveryAndSignIn = {
            navController.navigate(Root.Register.Recovery.Home)
        },
        onSynchronization = {

        },
    )
}

@RouteGraphDestination(
    route = Root.Register.CreateIdentity.path,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun CreateIdentityRoute(
    @Path("personaName") personaName: String?,
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    CreateIdentityHost(
        personaName = personaName.orEmpty(),
        onDone = {
            navController.navigate(Uri.parse("maskwallet://Home/Personas"), navOptions = navOptions {
                launchSingleTop = true
                popUpTo("Home") {
                    inclusive = false
                }
            })
        },
        onBack = {
            onBack.invoke()
        }
    )
}

@RouteGraphDestination(
    route = Root.Register.WelcomeCreatePersona,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun WelcomeCreatePersonaRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    CreatePersonaScene(
        onBack = {
            onBack.invoke()
        },
        onDone = { name ->
            navController.navigate(Root.Register.CreateIdentity(name))
        }
    )
}

@RouteGraphDestination(
    route = Root.Register.CreatePersona,
    packageName = RouteType.Modal.PackageName,
    functionName = RouteType.Modal.FunctionName,
)
@Composable
fun CreatePersonaRoute(
    navController: NavController,
) {
    CreatePersonaModal(
        onDone = { name ->
            navController.navigate(Root.Register.CreateIdentity(name))
        }
    )
}

@RouteGraphDestination(
    route = Root.Register.Recovery.Home,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun RecoveryHomeRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    RecoveryHomeScene(
        onBack = {
            onBack.invoke()
        },
        onIdentity = {
            navController.navigate(Root.Register.Recovery.Identity)
        },
        onPrivateKey = {
            navController.navigate(Root.Register.Recovery.PrivateKey)
        },
        onLocalBackup = {
            navController.navigate(Root.Register.Recovery.LocalBackup.LocalBackup_PickFile)
        },
        onRemoteBackup = {
            navController.navigate(Root.Register.Recovery.RemoteBackupRecovery.RemoteBackupRecovery_Email)
        }
    )
}

@RouteGraphDestination(
    route = Root.Register.Recovery.LocalBackup.RemoteBackupRecovery_RecoveryLocal.path,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun RemoteBackupRecovery_RecoveryLocalRoute(
    @Path("uri") uri: String?,
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    if (uri != null) {
        RecoveryLocalHost(
            uri = Uri.parse(uri),
            onBack = {
                onBack.invoke()
            },
            onConfirm = {
                navController.navigate(Root.Register.Recovery.Complected) {
                    popUpTo(Root.Register.Init) {
                        inclusive = true
                    }
                }
            }
        )
    }
}

@RouteGraphDestination(
    route = Root.Register.Recovery.LocalBackup.LocalBackup_PickFile,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun LocalBackup_PickFileRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                navController.navigate(
                    Root.Register.Recovery.LocalBackup.RemoteBackupRecovery_RecoveryLocal(it.toString())
                ) {
                    popUpTo(Root.Register.Recovery.LocalBackup.LocalBackup_PickFile) {
                        inclusive = true
                    }
                }
            } else {
                onBack.invoke()
            }
        },
    )
    LaunchedEffect(Unit) {
        filePickerLauncher.launch(arrayOf("*/*"))
    }
}

@RouteGraphDestination(
    route = Root.Register.Recovery.Identity,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun IdentityRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val viewModel: IdentityViewModel = getViewModel()
    val identity by viewModel.identity.observeAsState(initial = "")
    IdentityScene(
        identity = identity,
        onIdentityChanged = {
            viewModel.setIdentity(it)
        },
        onConfirm = {
            viewModel.onConfirm()
            navController.navigate(Root.Register.Recovery.Complected) {
                popUpTo(Root.Register.Init) {
                    inclusive = true
                }
            }
        },
        onBack = {
            onBack.invoke()
        },
    )
}

@RouteGraphDestination(
    route = Root.Register.Recovery.PrivateKey,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun PrivateKeyRoute(
    navController: NavController,
    @Back onBack: () -> Unit
) {
    val viewModel: PrivateKeyViewModel = getViewModel()
    val privateKey by viewModel.privateKey.observeAsState(initial = "")
    PrivateKeyScene(
        privateKey = privateKey,
        onPrivateKeyChanged = {
            viewModel.setPrivateKey(it)
        },
        onConfirm = {
            viewModel.onConfirm()
            navController.navigate(Root.Register.Recovery.Complected) {
                popUpTo(Root.Register.Init) {
                    inclusive = true
                }
            }
        },
        onBack = {
            onBack.invoke()
        },
    )
}

@RouteGraphDestination(
    route = Root.Register.Recovery.Complected,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun ComplectedRoute(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    RecoveryComplectedScene(
        onBack = {
            onBack.invoke()
        },
        onConfirm = {
            navController.navigate(Root.Main.Home) {
                popUpTo(Root.Register.Init) {
                    inclusive = true
                }
            }
        },
    )
}
