package com.dimension.maskbook.wallet.ui.scenes.wallets.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskPermissionsRequired
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.journeyapps.barcodescanner.BarcodeView

@Composable
fun ScanQrcodeScene(
    onBack: () -> Unit,
    onResult: (result: String) -> Unit,
) {
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
                        Text(text = stringResource(R.string.scene_scan_qr_code_title))
                    }
                )
            }
        ) {
            val context = LocalContext.current
            val barcodeView = remember {
                BarcodeView(context).apply {
                    this.decodeSingle { result ->
                        result.text?.let { barCodeOrQr ->
                            onResult(barCodeOrQr)
                        }
                    }
                }
            }
            DisposableEffect(Unit) {
                onDispose {
                    barcodeView.apply {
                        stopDecoding()
                        pause()
                    }
                }
            }
            Box {
                MaskPermissionsRequired(
                    permissions = listOf(android.Manifest.permission.CAMERA),
                    onPermissionDenied = {
                        onBack.invoke()
                    },
                    feature = stringResource(R.string.scene_scan_qr_code_title),
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = {
                            barcodeView.apply {
                                resume()
                            }
                        }
                    )
                }

                ScannerMask(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun ScannerMask(
    modifier: Modifier = Modifier,
    maskColor: Color = Color.Black.copy(alpha = 0.5f),
    scannerSize: Dp = 284.dp,
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val size = this.size
            val scannerPath = Path().apply {
                addRect(
                    Rect(
                        offset = Offset(
                            x = (size.width - scannerSize.toPx()) / 2,
                            y = (size.height - scannerSize.toPx()) / 2
                        ), size = Size(scannerSize.toPx(), scannerSize.toPx())
                    )
                )
            }
            clipPath(scannerPath, clipOp = ClipOp.Difference) {
                drawRect(color = maskColor)
            }
        }
        Box(modifier = Modifier.size(scannerSize).align(Alignment.Center)) {
            Image(
                painter = painterResource(R.drawable.ic_scanner),
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Scanner",
                contentScale = ContentScale.FillBounds
            )
            with(LocalDensity.current) {
                val offset = remember { Animatable(10.dp.toPx()) }

                LaunchedEffect(offset) {
                    offset.animateTo(
                        targetValue = scannerSize.toPx() - 10.dp.toPx(),
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing),
                        )
                    )
                }
                Image(
                    painter = painterResource(R.drawable.ic_scanner_line),
                    modifier = Modifier.width(scannerSize).align(Alignment.TopCenter)
                        .offset(x = 0.dp, y = offset.value.toDp()),
                    contentDescription = "Scanner line",
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}