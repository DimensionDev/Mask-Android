package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun MaskCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colors.surface, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .then(modifier),
        content = content,
    )
}