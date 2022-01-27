package com.dimension.maskbook.wallet.ui.scenes.app

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.*


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LabsTransakScene(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
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
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        // TODO pass token ,wallet address and other params
                        loadUrl("https://staging-global.transak.com?defaultCryptoCurrency=ETH")
                    }
                }
            )
        }
    }
}

