package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MaskModal(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .animateContentSize()
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .background(Color(0XFFCBD1D9), shape = RoundedCornerShape(99.dp))
        )
        Spacer(modifier = Modifier.height(18.dp))
        content.invoke()
    }
}