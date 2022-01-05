package com.dimension.maskbook.wallet.ui.scenes.register.createidentity

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.dialog
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.scenes.register.BackupIdentityScene
import com.dimension.maskbook.wallet.ui.scenes.register.WelcomeScene
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.viewmodel.register.CreateIdentityViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreateIdentityHost(
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val navController = rememberAnimatedNavController()
    val viewModel: CreateIdentityViewModel = getViewModel()
    AnimatedNavHost(
        navController = navController,
        startDestination = "Backup",
        route = "CreateIdentity",
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
        composable("Backup") {
            val words by viewModel.words.observeAsState(emptyList())
            BackupIdentityScene(
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
            VerifyIdentityScene(
                words = wordsInRandomOrder,
                onBack = {
                    viewModel.clearWords()
                    navController.popBackStack()
                },
                onClear = { viewModel.clearWords() },
                onConfirm = {
                    navController.navigate("Confirm")
                },
                onWordSelected = {
                    viewModel.selectWord(it)
                },
                selectedWords = selectedWords,
                correct = correct,
                onWordDeselected = {
                    viewModel.deselectWord(it)
                }
            )
        }
        composable("Welcome") {
            val persona by viewModel.persona.observeAsState(initial = "")
            WelcomeScene(
                persona = persona,
                onPersonaChanged = {
                    viewModel.setPersona(it)
                },
                onNext = {
                    viewModel.confirm()
                    onDone.invoke()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        dialog("Confirm") {
            MaskDialog(
                onDismissRequest = {

                },
                icon = {
                    Image(
                        painterResource(id = R.drawable.ic_property_1_snccess),
                        contentDescription = null
                    )
                },
                title = {
                    Text(text = "Identity created!")
                },
                text = {
                    Text(text = "Please don’t forget your identity code. Your identity code is the only proof of your user identity and the only basis for you to encrypt or decrypt social activities. ")
                },
                buttons = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate("Welcome") {
                                popUpTo("Backup") {
                                    inclusive = true
                                }
                            }
                        },
                    ) {
                        Text(text = "Done")
                    }
                },
            )
        }
    }
}