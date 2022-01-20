package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ui.isDarkTheme

@Composable
fun MaskModal(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Spacer(
            modifier = Modifier
                .size(40.dp, 4.dp)
                .background(
                    if (isDarkTheme()) {
                        MaskModalDefaults.lineDarkColor
                    } else {
                        MaskModalDefaults.lineLightColor
                    }, shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.height(20.dp))
        content.invoke()
    }
}

private object MaskModalDefaults {
    val lineLightColor = Color(0xFFCBD1D9)
    val lineDarkColor = Color(0xFF2C354C)
}
