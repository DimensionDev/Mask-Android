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
import android.os.Bundle
import android.view.WindowManager
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.LocalImageLoader
import com.dimension.maskbook.common.gecko.PromptFeatureDelegate
import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.common.manager.ImageLoaderManager
import com.dimension.maskbook.common.ui.widget.LocalWindowInsetsController
import com.dimension.maskbook.entry.ui.App
import com.google.accompanist.insets.ProvideWindowInsets
import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class ComposeActivity : PreComposeActivity() {

    private lateinit var promptFeature: PromptFeatureDelegate
    private val imageLoaderManager: ImageLoaderManager by inject()

    private val windowInsetsControllerCompat by lazy {
        WindowInsetsControllerCompat(window, window.decorView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        promptFeature = get<WebContentController>().createPromptFeature(this)
        setContent {
            CompositionLocalProvider(
                LocalImageLoader provides imageLoaderManager.imageLoader,
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

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        promptFeature.onActivityResult(requestCode, data, resultCode)
    }
}
