package com.dimension.maskbook.common

import androidx.navigation.NavController
import org.koin.core.module.Module

interface ModuleSetup {
    fun route(navController: NavController)
    fun dependencyInject(): Module
}