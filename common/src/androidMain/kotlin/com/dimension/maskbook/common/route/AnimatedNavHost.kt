package com.dimension.maskbook.common.route

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavController
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.RouteBuilder

@Composable
fun AnimatedNavHost(
    navController: NavController,
    initialRoute: String,
    builder: RouteBuilder.() -> Unit,
) {
    NavHost(
        navController = navController,
        initialRoute = initialRoute,
        builder = builder,
    )
}