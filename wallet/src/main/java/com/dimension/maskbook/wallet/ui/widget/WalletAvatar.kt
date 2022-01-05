package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dimension.maskbook.wallet.repository.WalletData

@Composable
fun WalletAvatar(
    walletData: WalletData,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colors.primary, shape = CircleShape)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = walletData.name.firstOrNull()?.toString() ?: "", color = Color.White)
    }
}