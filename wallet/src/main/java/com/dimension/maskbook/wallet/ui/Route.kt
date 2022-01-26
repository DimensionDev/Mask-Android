package com.dimension.maskbook.wallet.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.dimension.maskbook.wallet.navHostAnimationDurationMillis
import com.dimension.maskbook.wallet.route.backupRoute
import com.dimension.maskbook.wallet.route.generatedRoute
import com.dimension.maskbook.wallet.route.settingsRoute
import com.dimension.maskbook.wallet.route.walletsRoute
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

val LocalRootNavController =
    staticCompositionLocalOf<NavHostController> { error("No NavHostController") }

object RouteType {
    object Composable {
        const val PackageName = "com.dimension.maskbook.wallet.ext"
        const val FunctionName = "animatedComposable"
    }
    object Dialog {
        const val PackageName = "androidx.navigation.compose"
        const val FunctionName = "dialog"
    }
    object Modal {
        const val PackageName = "com.dimension.maskbook.wallet.ext"
        const val FunctionName = "modal"
    }
}


@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Route(
    startDestination: String = "Register",
    onBack: () -> Unit,
) {
    val navController = rememberAnimatedNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    CompositionLocalProvider(LocalRootNavController provides navController) {
        ModalBottomSheetLayout(
            bottomSheetNavigator,
            sheetBackgroundColor = MaterialTheme.colors.background,
        ) {
            AnimatedNavHost(
                navController = navController,
                startDestination = startDestination,
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
                walletsRoute(navController = navController)
                settingsRoute(navController = navController)
                backupRoute(navController = navController)
                generatedRoute(navController)
            }
        }
    }
}
