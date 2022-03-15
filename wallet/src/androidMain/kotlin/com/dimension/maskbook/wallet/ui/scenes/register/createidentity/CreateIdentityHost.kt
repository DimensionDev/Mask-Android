/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.ui.scenes.register.createidentity

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.dialog
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.RouteHost
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.scenes.register.BackupIdentityScene
import com.dimension.maskbook.wallet.viewmodel.register.CreateIdentityViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun CreateIdentityHost(
    personaName: String,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val navController = rememberAnimatedNavController()
    val viewModel: CreateIdentityViewModel = getViewModel {
        parametersOf(personaName)
    }
    RouteHost(
        navController = navController,
        startDestination = "Backup",
    ) {
        composable("Backup") {
            val words by viewModel.words.observeAsState(emptyList())
            BackupIdentityScene(
                words = words.map { it.word },
                onRefreshWords = {
                    viewModel.refreshWords()
                },
                onVerify = {
                    navController.navigate("Verify")
                },
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
                    Text(text = stringResource(R.string.common_alert_identity_create_title))
                },
                text = {
                    Text(text = stringResource(R.string.common_alert_identity_create_description))
                },
                buttons = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.confirm()
                            onDone.invoke()
                        },
                    ) {
                        Text(text = stringResource(R.string.common_controls_done))
                    }
                },
            )
        }
    }
}
