package com.dimension.maskbook.wallet.ui.scenes.app

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.model.TransakConfig
import com.dimension.maskbook.wallet.ui.widget.*


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LabsTransakScene(
    onBack: () -> Unit,
    transakConfig: TransakConfig
) {
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

