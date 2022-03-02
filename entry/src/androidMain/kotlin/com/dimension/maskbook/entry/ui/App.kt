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
package com.dimension.maskbook.entry.ui

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.dimension.maskbook.common.CommonSetup
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.entry.BuildConfig
import com.dimension.maskbook.entry.EntrySetup
import com.dimension.maskbook.entry.repository.EntryRepository
import com.dimension.maskbook.entry.route.EntryRoute
import com.dimension.maskbook.entry.ui.scene.Splash
import com.dimension.maskbook.extension.ExtensionSetup
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.labs.LabsSetup
import com.dimension.maskbook.persona.PersonaSetup
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.setting.SettingSetup
import com.dimension.maskbook.wallet.WalletSetup
import com.dimension.maskbook.wallet.route.WalletRoute
import kotlinx.coroutines.flow.firstOrNull
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.get
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App(
    onFinish: () -> Unit,
) {
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var startDestination by rememberSaveable { mutableStateOf(CommonRoute.WebContent) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        warmingUp(context)
        startDestination = getStartDestination()
        showSplash = false
    }
    AnimatedContent(showSplash) {
        if (it) {
            Splash()
        } else {
            Router(
                startDestination = startDestination,
                onFinish = onFinish,
            )
        }
    }
}

suspend fun getStartDestination(): String {
    val repository = get<EntryRepository>()
    val shouldShowEntry = repository.shouldShowEntry.firstOrNull() ?: true
    if (shouldShowEntry) {
        return EntryRoute.Intro
    }
    val persona = get<PersonaServices>().currentPersona.firstOrNull()
    return if (persona == null) {
        CommonRoute.Main.Home(CommonRoute.Main.Tabs.Persona)
    } else {
        WalletRoute.Register.Init
    }
}

private suspend fun warmingUp(context: Context) {
    startKoin {
        androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
        androidContext(context.applicationContext)
        modules(
            CommonSetup.dependencyInject(),
            WalletSetup.dependencyInject(),
            SettingSetup.dependencyInject(),
            LabsSetup.dependencyInject(),
            PersonaSetup.dependencyInject(),
            EntrySetup.dependencyInject(),
            ExtensionSetup.dependencyInject(),
        )
    }
    get<ExtensionServices>().ensureExtensionActive()
    CommonSetup.onExtensionReady()
    WalletSetup.onExtensionReady()
    SettingSetup.onExtensionReady()
    LabsSetup.onExtensionReady()
    PersonaSetup.onExtensionReady()
    EntrySetup.onExtensionReady()
    ExtensionSetup.onExtensionReady()
}
