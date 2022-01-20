package com.dimension.maskbook.wallet.ui.scenes.wallets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.*
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

@Composable
fun WalletQrcodeScene(
    address: String,
    name: String,
    onShare: () -> Unit,
    onBack: () -> Unit,
    onCopy: () -> Unit,
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
                        Text(text = "Receive Ethereum Assets")
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ScaffoldPadding),
            ) {
                Card {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(25.dp)
                                .aspectRatio(1f)
                        ) {
                            val bitmap = remember(address) {
                                val barcodeEncoder = BarcodeEncoder()
                                barcodeEncoder.encodeBitmap(
                                    address,
                                    BarcodeFormat.QR_CODE,
                                    400,
                                    400
                                )
                            }
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillWidth,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                R.string.scene_wallet_receive_your_address,
                                name,
                            ),
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                                .background(Color(0xFFF6F8FB), shape = RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .clickable {
                                    onCopy.invoke()
                                }
                        ) {
                            Text(text = address, textAlign = TextAlign.Center)
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    onClick = { onShare.invoke() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.scene_wallet_receive_btn_share))
                }
            }
        }
    }
}