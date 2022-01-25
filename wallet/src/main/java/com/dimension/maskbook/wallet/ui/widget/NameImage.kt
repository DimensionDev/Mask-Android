package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun NameImage(
    name: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
) {
    BoxWithConstraints(
        modifier = modifier
            .alpha(LocalContentAlpha.current)
            .background(MaterialTheme.colors.primary, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val combinedTextStyle = style ?: LocalTextStyle.current.copy(
            fontSize = (maxHeight / 2).value.sp
        )
        Text(
            text = name.firstOrNull()?.toString() ?: "N",
            style = combinedTextStyle,
            color = MaterialTheme.colors.onPrimary,
        )
    }
}
