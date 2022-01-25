package com.dimension.maskbook.wallet.ext

import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle

fun applyTextStyle(
    textStyle: TextStyle,
    content: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (content == null) return null
    return {
        CompositionLocalProvider(
            LocalTextStyle provides textStyle,
            content = content
        )
    }
}
