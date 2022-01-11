package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.scenes.persona.social.tabIndicatorOffset3
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.wallets.WalletConnectViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.koin.androidx.compose.getViewModel

enum class WalletConnectType {
    Manually,
    QRCode,
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WalletConnectModal() {
    val navController = rememberAnimatedNavController()
    val viewModel = getViewModel<WalletConnectViewModel>()
    val qrCode by viewModel.qrCode.observeAsState(initial = "")
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding),
        ) {
            Text(
                text = "WalletConnect",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            AnimatedNavHost(
                navController = navController,
                startDestination = "WalletConnectTypeSelect"
            ) {
                composable("WalletConnectTypeSelect") {
                    TypeSelectScene(qrCode = qrCode, onCopy = {
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(
                                "WalletConnect Uri",
                                it
                            )
                        )
                    })
                }

                composable("WalletConnectConnecting") {
                    Connecting()
                }

                composable("WalletConnectFailed") {
                    WalletConnectFailure(
                        onRetry = {

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WalletConnectFailure(
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFFF5F5F)),
            contentAlignment = Alignment.Center,
        ) {
            Image(painterResource(id = R.drawable.ic_close_square), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Connection failed.", color = Color(0xFFFF5F5F))
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(onClick = onRetry) {
            Text(text = "Try Again")
        }
    }
}

@Composable
fun Connecting() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(id = R.drawable.ic_mask1), contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(26.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(painterResource(id = R.drawable.mask1), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Connecting...")
    }
}


@Composable
private fun TypeSelectScene(
    qrCode: String,
    onCopy: (String) -> Unit
) {
    Column {
        var selectedTabIndex by remember {
            mutableStateOf(0)
        }
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = MaterialTheme.colors.background,
            divider = {
                TabRowDefaults.Divider(thickness = 0.dp)
            },
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(0.1f)
                            .fillMaxHeight()
                            .background(
                                color = MaterialTheme.colors.primary,
                                shape = RoundedCornerShape(99.dp)
                            )
                    )
                }
            },
        ) {
            WalletConnectType.values().forEachIndexed { index, type ->
                Tab(
                    text = { Text(type.name) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                    },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                        alpha = ContentAlpha.medium
                    ),
                )
            }
        }
        when (WalletConnectType.values()[selectedTabIndex]) {
            WalletConnectType.Manually -> WalletConnectManually()
            WalletConnectType.QRCode -> WalletConnectQRCode(qrCode = qrCode, onCopy = onCopy)
        }
    }
}

@Composable
fun WalletConnectQRCode(qrCode: String, onCopy: (String) -> Unit) {
    val qrCodeBitmap = remember(qrCode) {
        try {
            BarcodeEncoder().encodeBitmap(
                qrCode,
                BarcodeFormat.QR_CODE,
                500,
                500
            )
        } catch (e: Throwable) {
            null
        }
    }
    Text(text = "Use a WalletConnect compatible wallet to scan the QR Code")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCopy.invoke(qrCode) }
            .aspectRatio(1f)
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_qr_code_border),
            modifier = Modifier.fillMaxSize(),
            contentDescription = ""
        )
        Image(
            painter = rememberImagePainter(data = qrCodeBitmap),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            contentDescription = "qrCode"
        )
    }
    Text(text = "Tap to copy to clipboard")
}

@Composable
fun WalletConnectManually() {
    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = MaterialTheme.colors.background,
        indicator = { tabPositions ->
            Box(
                Modifier
                    .tabIndicatorOffset3(tabPositions[selectedTabIndex])
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        },
        edgePadding = 14.dp,
        divider = { },
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        supportedChainType.forEachIndexed { index, type ->
            val selected = selectedTabIndex == index
            Tab(
                text = { Text(type.name) },
                selected = selected,
                onClick = {
                    selectedTabIndex = index
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                    alpha = ContentAlpha.medium
                ),
            )
        }

    }
}
