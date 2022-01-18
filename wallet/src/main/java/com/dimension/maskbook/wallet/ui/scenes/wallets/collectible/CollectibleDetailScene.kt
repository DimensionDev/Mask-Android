package com.dimension.maskbook.wallet.ui.scenes.wallets.collectible

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.WalletCollectibleData
import com.dimension.maskbook.wallet.ui.widget.*

@Composable
fun CollectibleDetailScene(
    data: WalletCollectibleData,
    onBack: () -> Unit,
    onSend: () -> Unit,
    onReceive: () -> Unit,
) {
    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar(
                navigationIcon = {
                    MaskBackButton(
                        onBack = onBack,
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(ScaffoldPadding)
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                data.items.firstOrNull()?.let { item ->
                    if (!item.videoUrl.isNullOrEmpty()) {
                        // TODO: video
                    } else if (!item.imageUrl.isNullOrEmpty()) {
                        Image(
                            painter = rememberImagePainter(item.imageUrl),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp)),
                            contentDescription = null
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.mask),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp)),
                            contentDescription = null
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onSend.invoke() },
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFFFFB915))
                ) {
                    Icon(painterResource(id = R.drawable.upload), contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.scene_wallet_balance_btn_Send), maxLines = 1)
                }
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onReceive.invoke() },
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                ) {
                    Icon(
                        painterResource(id = R.drawable.download),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.scene_wallet_balance_btn_receive), maxLines = 1)
                }
            }
        }
    }
}