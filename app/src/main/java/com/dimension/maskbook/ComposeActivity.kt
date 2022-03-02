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

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.decode.SvgDecoder
import com.dimension.maskbook.common.ui.widget.LocalWindowInsetsController
import com.dimension.maskbook.entry.ui.App
import com.google.accompanist.insets.ProvideWindowInsets
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope

// TOOD: Change to FragmentActivity, blocked by https://github.com/InsertKoinIO/koin/pull/1287
class ComposeActivity : AppCompatActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()
    private val windowInsetsControllerCompat by lazy {
        WindowInsetsControllerCompat(window, window.decorView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
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
                        onFinish = { finish() },
                    )
                }
            }
        }
    }
}
