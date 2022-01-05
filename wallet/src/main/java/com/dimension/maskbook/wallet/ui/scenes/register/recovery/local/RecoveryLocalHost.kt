package com.dimension.maskbook.wallet.ui.scenes.register.recovery.local

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.dialog
import androidx.navigation.plusAssign
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.BackupMeta
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.settings.MetaItem
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.recovery.RecoveryLocalViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class,
)
@Composable
fun RecoveryLocalHost(
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    uri: Uri
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController()
    navController.navigatorProvider += bottomSheetNavigator
    val viewModel: RecoveryLocalViewModel = getViewModel {
        parametersOf(uri)
    }
    val loadState by viewModel.loadState.observeAsState(initial = RecoveryLocalViewModel.LoadState.Loading)
    LaunchedEffect(Unit) {
        snapshotFlow { loadState }
            .distinctUntilChanged()
            .collect {
                when (it) {
                    RecoveryLocalViewModel.LoadState.Loading -> {
                        if (navController.currentDestination?.route != "Loading") {
                            navController.navigate("Loading") {
                                popUpTo("Loading") {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    RecoveryLocalViewModel.LoadState.Failed -> navController.navigate("Failed") {
                        popUpTo("Loading") {
                            inclusive = true
                        }
                    }
                    RecoveryLocalViewModel.LoadState.Success -> navController.navigate("Success") {
                        popUpTo("Loading") {
                            inclusive = true
                        }
                    }
                    RecoveryLocalViewModel.LoadState.RequirePassword -> {
                        navController.navigate("Password")
                    }
                }
            }
    }
    LaunchedEffect(bottomSheetNavigator.navigatorSheetState.isVisible) {
        if (loadState == RecoveryLocalViewModel.LoadState.RequirePassword && !bottomSheetNavigator.navigatorSheetState.isVisible) {
            onBack.invoke()
        }
    }
    ModalBottomSheetLayout(
        bottomSheetNavigator,
        sheetBackgroundColor = MaterialTheme.colors.background,
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = "Loading",
            route = "RecoveryLocal",
            enterTransition = { _, _ ->
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween())
            },
            exitTransition = { _, _ ->
                slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween())
            },
            popEnterTransition = { _, _ ->
                slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween())
            },
            popExitTransition = { _, _ ->
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween())
            },
        ) {
            bottomSheet("Password") {
                val password by viewModel.password.observeAsState(initial = "")
                val error by viewModel.passwordError.observeAsState(initial = false)
                MaskModal {
                    Column(
                        modifier = Modifier
                            .padding(ScaffoldPadding),
                    ) {
                        Text(text = "Backup password")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = password,
                            onValueChange = {
                                viewModel.setPassword(it)
                            },
                        )
                        if (error) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Incorrect Password", color = Color.Red)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.confirmPassword() },
                        ) {
                            Text(text = "Next")
                        }
                    }
                }
            }
            composable("Loading") {
                ImportingScene(
                    onBack = {
                        onBack.invoke()
                    }
                )
            }
            dialog("Failed") {
                MaskDialog(
                    onDismissRequest = {
                    },
                    title = {
                        Text(text = "Unsupported data backup")
                    },
                    text = {
                        Text(text = "Please examine your data backup.")
                    },
                    buttons = {
                        PrimaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onBack.invoke() },
                        ) {
                            Text(text = "OK")
                        }
                    },
                    icon = {
                        Image(
                            painterResource(id = R.drawable.ic_property_1_failed),
                            contentDescription = null
                        )
                    }
                )
            }
            composable("Success") {
                ImportSuccessScene(
                    viewModel = viewModel,
                    onBack = {
                        onBack.invoke()
                    },
                    onConfirm = {
                        navController.navigate("Notification")
                    }
                )
            }
            dialog("Notification") {
                MaskDialog(
                    onDismissRequest = { },
                    icon = {
                        Image(
                            painterResource(id = R.drawable.ic_property_1_note),
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(text = "You have successfully verified your cloud password and the backup is being uploaded. To unify backup passwords, do you want to synchronize your cloud password as local backup password?")
                    },
                    buttons = {
                        Row {
                            SecondaryButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Text(text = "Cancel")
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            PrimaryButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onConfirm.invoke()
                                },
                            ) {
                                Text(text = "Confirm")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ImportSuccessScene(
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    viewModel: RecoveryLocalViewModel,
) {
    val meta by viewModel.meta.observeAsState(initial = null)
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    title = {
                        Text(text = "Restore backups")
                    },
                    navigationIcon = {
                        MaskBackButton {
                            onBack.invoke()
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ScaffoldPadding)
            ) {
                meta?.let { meta ->
                    BackMetaDisplay(meta)
                }
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.restore()
                        onConfirm.invoke()
                    }
                ) {
                    Text(text = "Restore Backups")
                }
            }
        }
    }
}

@Composable
fun ImportingScene(
    onBack: () -> Unit
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton {
                            onBack.invoke()
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Text(text = "Loading...")
            }
        }
    }
}