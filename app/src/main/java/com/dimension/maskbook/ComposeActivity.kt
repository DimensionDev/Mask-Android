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
package com.dimension.maskbook

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.decode.SvgDecoder
import com.dimension.maskbook.common.CommonSetup
import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.common.ui.widget.LocalWindowInsetsController
import com.dimension.maskbook.entry.BuildConfig
import com.dimension.maskbook.entry.EntrySetup
import com.dimension.maskbook.entry.ui.App
import com.dimension.maskbook.extension.ExtensionSetup
import com.dimension.maskbook.labs.LabsSetup
import com.dimension.maskbook.persona.PersonaSetup
import com.dimension.maskbook.setting.SettingSetup
import com.dimension.maskbook.wallet.WalletSetup
import com.google.accompanist.insets.ProvideWindowInsets
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ComposeActivity : FragmentActivity() {
    private val permissionsRequest: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            promptFeature.onPermissionsResult(
                it.keys.toTypedArray(),
                it.values.map { if (it) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED }
                    .toIntArray()
            )
        }
    private val promptFeature by lazy {
        get<WebContentController>().createPromptFeature(this) {
            permissionsRequest.launch(it)
        }
    }
    private val windowInsetsControllerCompat by lazy {
        WindowInsetsControllerCompat(window, window.decorView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(applicationContext)
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
        setContent {
            CompositionLocalProvider(
                LocalImageLoader provides ImageLoader.Builder(this).componentRegistry {
                    add(SvgDecoder(this@ComposeActivity))
                }.build(),
                LocalWindowInsetsController provides windowInsetsControllerCompat,
            ) {
                ProvideWindowInsets(
                    windowInsetsAnimationsEnabled = true
                ) {
                    App(
                        onInitialized = {
                            promptFeature.start()
                        },
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!promptFeature.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        promptFeature.onActivityResult(requestCode, data, resultCode)
    }
}
