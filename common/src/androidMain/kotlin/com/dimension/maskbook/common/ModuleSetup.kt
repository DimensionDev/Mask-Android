package com.dimension.maskbook.common

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import org.koin.core.module.Module

interface ModuleSetup {
    fun NavGraphBuilder.route(navController: NavController)
    fun dependencyInject(): Module
}