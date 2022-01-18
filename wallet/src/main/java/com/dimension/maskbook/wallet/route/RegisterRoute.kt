package com.dimension.maskbook.wallet.route

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.ui.scenes.register.CreatePersonaModal
import com.dimension.maskbook.wallet.ui.scenes.register.CreatePersonaScene
import com.dimension.maskbook.wallet.ui.scenes.register.RegisterScene
import com.dimension.maskbook.wallet.ui.scenes.register.createidentity.CreateIdentityHost
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.IdentityScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.PrivateKeyScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryComplectedScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.RecoveryHomeScene
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.local.RecoveryLocalHost
import com.dimension.maskbook.wallet.ui.scenes.register.recovery.remote.remoteBackupRecovery
import com.dimension.maskbook.wallet.viewmodel.recovery.IdentityViewModel
import com.dimension.maskbook.wallet.viewmodel.recovery.PrivateKeyViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@ExperimentalAnimationApi
@ExperimentalMaterialNavigationApi
fun NavGraphBuilder.registerRoute(
    navController: NavController,
) {
    navigation(
        route = "Register",
        startDestination = "Init",
    ) {
        composable(
            "Init",
        ) {
            val repository = get<IPersonaRepository>()
            val persona by repository.currentPersona.observeAsState(initial = null)
            LaunchedEffect(Unit) {
                snapshotFlow { persona }
                    .distinctUntilChanged()
                    .collect {
                        if (it != null) {
                            navController.navigate("Main") {
                                popUpTo("Register") {
                                    inclusive = true
                                }
                            }
                        }
                    }
            }
            RegisterScene(
                onCreateIdentity = {
                    navController.navigate("WelcomeCreatePersona")
                },
                onRecoveryAndSignIn = {
                    navController.navigate("Recovery")
                },
                onSynchronization = {

                },
            )
        }
        composable(
            route = "CreateIdentity/{personaName}",
            arguments = listOf(
                navArgument("personaName") { type = NavType.StringType }
            )
        ) {
            CreateIdentityHost(
                personaName = it.arguments?.getString("personaName").orEmpty(),
                onDone = {
                    // navController.navigate("Main") {
                    //     launchSingleTop = true
                    //     popUpTo("Home") {
                    //         inclusive = false
                    //     }
                    // }
                    navController.navigate(Uri.parse("maskwallet://Home/Personas"), navOptions = navOptions {
                        launchSingleTop = true
                        popUpTo("Home") {
                            inclusive = false
                        }
                    })
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("WelcomeCreatePersona") {
            CreatePersonaScene(
                onBack = {
                    navController.popBackStack()
                },
                onDone = { name ->
                    navController.navigate("CreateIdentity/${name.encodeUrl()}")
                }
            )
        }
        bottomSheet("CreatePersona") {
            CreatePersonaModal(
                onDone = { name ->
                    navController.navigate("CreateIdentity/${name.encodeUrl()}")
                }
            )
        }
        navigation(startDestination = "Home", route = "Recovery") {
            composable("Home") {
                RecoveryHomeScene(
                    onBack = {
                        navController.popBackStack()
                    },
                    onIdentity = {
                        navController.navigate("Identity")
                    },
                    onPrivateKey = {
                        navController.navigate("PrivateKey")
                    },
                    onLocalBackup = {
                        navController.navigate("LocalBackup")
                    },
                    onRemoteBackup = {
                        navController.navigate("RemoteBackupRecovery")
                    }
                )
            }
            navigation("RemoteBackupRecovery_Email", "RemoteBackupRecovery") {
                remoteBackupRecovery(navController)
            }
            navigation("LocalBackup_PickFile", "LocalBackup") {
                composable(
                    "RemoteBackupRecovery_RecoveryLocal/{uri}",
                    arguments = listOf(
                        navArgument("uri") { type = NavType.StringType },
                    )
                ) {
                    val uri = it.arguments?.getString("uri")
                        ?.let { Uri.parse(it) }
                    if (uri != null) {
                        RecoveryLocalHost(
                            uri = uri,
                            onBack = {
                                navController.popBackStack()
                            },
                            onConfirm = {
                                navController.navigate("Complected") {
                                    popUpTo("Init") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
                composable("LocalBackup_PickFile") {
                    val filePickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.OpenDocument(),
                        onResult = {
                            if (it != null) {
                                navController.navigate(
                                    "RemoteBackupRecovery_RecoveryLocal/${
                                        it.toString().encodeUrl()
                                    }"
                                ) {
                                    popUpTo("LocalBackup_PickFile") {
                                        inclusive = true
                                    }
                                }
                            } else {
                                navController.popBackStack()
                            }
                        },
                    )
                    LaunchedEffect(Unit) {
                        filePickerLauncher.launch(arrayOf("*/*"))
                    }
                }
            }
            composable("Identity") {
                val viewModel: IdentityViewModel = getViewModel()
                val identity by viewModel.identity.observeAsState(initial = "")
                IdentityScene(
                    identity = identity,
                    onIdentityChanged = {
                        viewModel.setIdentity(it)
                    },
                    onConfirm = {
                        viewModel.onConfirm()
                        navController.navigate("Complected") {
                            popUpTo("Init") {
                                inclusive = true
                            }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                )
            }
            composable("PrivateKey") {
                val viewModel: PrivateKeyViewModel = getViewModel()
                val privateKey by viewModel.privateKey.observeAsState(initial = "")
                PrivateKeyScene(
                    privateKey = privateKey,
                    onPrivateKeyChanged = {
                        viewModel.setPrivateKey(it)
                    },
                    onConfirm = {
                        viewModel.onConfirm()
                        navController.navigate("Complected") {
                            popUpTo("Init") {
                                inclusive = true
                            }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                )
            }
            composable("Complected") {
                RecoveryComplectedScene(
                    onBack = {
                        navController.popBackStack()
                    },
                    onConfirm = {
                        navController.navigate("Main") {
                            popUpTo("Register") {
                                inclusive = true
                            }
                        }
                    },
                )
            }

        }
//                    composable("Welcome") {
//                        val viewModel: WelcomeViewModel = getViewModel()
//                        val persona by viewModel.persona.observeAsState(initial = "")
//                        WelcomeScene(
//                            persona = persona,
//                            onPersonaChanged = {
//                                viewModel.setPersona(it)
//                            },
//                            onNext = {
//                                viewModel.onConfirm()
//                                navController.navigate("Main") {
//                                    popUpTo("Register") {
//                                        inclusive = true
//                                    }
//                                }
//                            },
//                            onBack = {
//                                navController.popBackStack()
//                            }
//                        )
//                    }
    }
}
