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
package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.navHostAnimationDurationMillis
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ImportWalletHost(
    wallet: String,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = "Import",
        route = "ImportWalletHost",
        enterTransition = { _, _ ->
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(navHostAnimationDurationMillis))
        },
        exitTransition = { _, _ ->
            slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(navHostAnimationDurationMillis))
        },
        popEnterTransition = { _, _ ->
            slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(navHostAnimationDurationMillis))
        },
        popExitTransition = { _, _ ->
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(navHostAnimationDurationMillis))
        },
    ) {
        composable("Import") {
            ImportWalletScene(
                onBack = { onBack.invoke() },
                onMnemonic = { navController.navigate("Mnemonic") },
                onPassword = { navController.navigate("PrivateKey") },
                onKeystore = { navController.navigate("Keystore") }
            )
        }

        composable("Mnemonic") {
            ImportWalletMnemonicScene(
                onBack = { navController.popBackStack() },
                wallet = wallet,
                onDone = { navController.navigate("DerivationPath/${it.encodeUrl()}") }
            )
        }

        composable("PrivateKey") {
            ImportWalletPrivateKeyScene(
                onBack = { navController.popBackStack() },
                wallet = wallet,
                onDone = { onDone.invoke() }
            )
        }

        composable("Keystore") {
            ImportWalletKeyStoreScene(
                onBack = { navController.popBackStack() },
                wallet = wallet,
                onDone = { onDone.invoke() }
            )
        }

        composable(
            "DerivationPath/{mnemonicCode}",
            arguments = listOf(navArgument("mnemonicCode") { type = NavType.StringType })
        ) {
            ImportWalletDerivationPathScene(
                onBack = { navController.popBackStack() },
                onDone = { onDone.invoke() },
                wallet = wallet,
                code = it.arguments?.getString("mnemonicCode")?.split(" ").orEmpty(),
            )
        }
    }
}
