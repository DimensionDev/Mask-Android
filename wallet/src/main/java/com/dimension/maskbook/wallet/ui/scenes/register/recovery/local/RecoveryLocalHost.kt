package com.dimension.maskbook.wallet.ui.scenes.register.recovery.local

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.dialog
import androidx.navigation.plusAssign
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.navHostAnimationDurationMillis
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.BackMetaDisplay
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.MaskInputField
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton
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
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(navHostAnimationDurationMillis))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(navHostAnimationDurationMillis))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(navHostAnimationDurationMillis))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(navHostAnimationDurationMillis))
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
                        Text(text = stringResource(R.string.scene_set_backup_password_backup_password))
                        Spacer(modifier = Modifier.height(8.dp))
                        MaskInputField(
                            modifier = Modifier.fillMaxWidth(),
                            value = password,
                            onValueChange = {
                                viewModel.setPassword(it)
                            },
                        )
                        if (error) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = stringResource(R.string.scene_restore_tip_incorrect_backup_password), color = Color.Red)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.confirmPassword() },
                        ) {
                            Text(text = stringResource(R.string.common_controls_next))
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
                        Text(text = stringResource(R.string.scene_restore_titles_unsupport_restore_data))
                    },
                    text = {
                        Text(text = stringResource(R.string.scene_restore_check_unsupport_data))
                    },
                    buttons = {
                        PrimaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onBack.invoke() },
                        ) {
                            Text(text = stringResource(R.string.common_controls_ok))
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
                        Text(text = stringResource(R.string.scene_restore_tip_remote_restore_succeed))
                    },
                    buttons = {
                        Row {
                            SecondaryButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Text(text = stringResource(R.string.common_controls_cancel))
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            PrimaryButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onConfirm.invoke()
                                },
                            ) {
                                Text(text = stringResource(R.string.common_controls_confirm))
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
                        Text(text = stringResource(R.string.scene_restore_titles_restore_buckup))
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
                    Text(text = stringResource(R.string.scene_restore_buttonTitles_backup))
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
                Text(text = stringResource(R.string.common_loading))
            }
        }
    }
}