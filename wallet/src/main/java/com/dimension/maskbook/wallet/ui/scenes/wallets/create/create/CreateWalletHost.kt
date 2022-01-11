package com.dimension.maskbook.wallet.ui.scenes.wallets.create.create

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.dialog
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.ui.scenes.register.createidentity.VerifyIdentityScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.Dialog
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.viewmodel.wallets.create.CreateWalletRecoveryKeyViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreateWalletHost(
    wallet: String,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val navController = rememberAnimatedNavController()
    val viewModel: CreateWalletRecoveryKeyViewModel = getViewModel()
    viewModel.setWallet(wallet)
    val result by viewModel.result.observeAsState(initial = null)
    AnimatedNavHost(
        navController = navController,
        startDestination = "Pharse",
        route = "CreateWalletHost",
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
        composable("Pharse") {
            val words by viewModel.words.observeAsState(initial = emptyList())
            MnemonicPhraseScene(
                words = words,
                onRefreshWords = {
                    viewModel.refreshWords()
                },
                onVerify = { navController.navigate("Verify") },
                onBack = onBack,
            )
        }
        composable("Verify") {
            val correct by viewModel.correct.observeAsState(initial = false)
            val selectedWords by viewModel.selectedWords.observeAsState(initial = emptyList())
            val wordsInRandomOrder by viewModel.wordsInRandomOrder.observeAsState(initial = emptyList())
            var showDialog by remember {
                mutableStateOf(false)
            }

            Box {
                VerifyIdentityScene(
                    words = wordsInRandomOrder,
                    onBack = {
                        viewModel.clearWords()
                        navController.popBackStack()
                    },
                    onClear = { viewModel.clearWords() },
                    onConfirm = {
                        viewModel.confirm()
                        showDialog = true
                    },
                    onWordSelected = {
                        viewModel.selectWord(it)
                    },
                    selectedWords = selectedWords,
                    correct = correct,
                    onWordDeselected = {
                        viewModel.deselectWord(it)
                    },
                    title = "Verify your phrase",
                    subTitle = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_identify_verify_description)
                )
                result?.let {
                    if (it.type == WalletCreateOrImportResult.Type.SUCCESS) {
                        navController.navigate("Confirm")
                    } else {
                        if (showDialog) {
                            it.Dialog(onDismissRequest = {showDialog = false})
                        }
                    }
                }
            }

        }
        dialog("Confirm") {
            MaskDialog(
                onDismissRequest = {
                    onDone.invoke()
                },
                icon = {
                    Image(
                        painterResource(id = R.drawable.ic_property_1_snccess),
                        contentDescription = null
                    )
                },
                title = {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_alert_wallet_create_success_title))
                },
                text = {
                    Text(text = "In order to protect your funds against hackers and thieves, store this mnemonic phrase in a safe and secure place.")
                },
                buttons = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onDone.invoke()
                        },
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                    }
                },
            )
        }
    }
}