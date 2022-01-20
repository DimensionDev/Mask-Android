package com.dimension.maskbook.wallet.ui.scenes.wallets.common

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun ScanQrcodeScene(
    onBack: () -> Unit,
    onResult: (result: String) -> Unit,
) {
    // TODO request camera permission
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(
                            onBack = onBack,
                        )
                    },
                    title = {
                        //TODO LOCALIZE
                        Text(text = "Scan the QR Code")
                    }
                )
            }
        ) {
            val context = LocalContext.current
            Box {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        BarcodeView(context).apply {
                            this.decodeSingle { result ->
                                result.text?.let { barCodeOrQr ->
                                    onResult(barCodeOrQr)
                                }
                            }
                            this.resume()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ScannerMask(
    modifier: Modifier = Modifier,
    maskColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
    scannerSize: Size = Size(width = 300.0f, height = 300.0f),
) {
    val offset = scannerSize.width
    Canvas(modifier = modifier) {
        drawRect(
            color = maskColor,
        )
        //TODO calculate position of rect
        drawRect(
            color = Color.Transparent,
            size = scannerSize,
        )
    }
}