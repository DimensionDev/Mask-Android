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
package com.dimension.maskbook.labs.ui.scenes

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.labs.R
import com.dimension.maskbook.labs.export.model.TransakConfig
import com.dimension.maskbook.labs.route.LabsRoute
import com.dimension.maskbook.wallet.export.WalletServices
import org.koin.androidx.compose.get

@NavGraphDestination(
    route = LabsRoute.LabsTransak,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
    deeplink = [Deeplinks.Labs.Transak]
)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LabsTransakScene(
    @Back onBack: () -> Unit,
) {
    val repo = get<WalletServices>()
    val currentWallet by repo.currentWallet.observeAsState(null)
    val transakConfig = remember(currentWallet) {
        TransakConfig(
            isStaging = false,
            walletAddress = currentWallet?.address ?: "",
            defaultCryptoCurrency = currentWallet?.tokens?.firstOrNull()?.tokenData?.symbol
                ?: "ETH",
        )
    }

    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    loading = true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loading = false
                }
            }
        }
    }
    LaunchedEffect(transakConfig.url) {
        webView.loadUrl(transakConfig.url)
    }
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    title = {
                        Text(stringResource(id = R.string.scene_app_plugins_transaction))
                    },
                    navigationIcon = {
                        MaskBackButton {
                            onBack.invoke()
                        }
                    }
                )
            }
        ) {
            Box {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        webView
                    }
                )
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
