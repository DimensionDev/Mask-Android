package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MaskListItem(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    val typography = MaterialTheme.typography
    val styleText = applyTextStyle(typography.h5, text)!!
    val styledSecondaryText = applyTextStyle(typography.body2, secondaryText)
    val styledOverlineText = applyTextStyle(typography.overline, overlineText)
    val styledTrailing = applyTextStyle(typography.body2, trailing)

    Row(
        modifier = modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(8.dp))
        }
        if (styledSecondaryText != null) {
            Column(Modifier.weight(1f)) {
                styleText()
                styledSecondaryText()
            }
        } else if (styledOverlineText != null) {
            Column(Modifier.weight(1f)) {
                styledOverlineText()
                styleText()
            }
        } else {
            Box(Modifier.weight(1f)) {
                styleText()
            }
        }
        if (styledTrailing != null) {
            Spacer(Modifier.width(8.dp))
            styledTrailing()
        }
    }
}

private fun applyTextStyle(
    textStyle: TextStyle,
    content: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (content == null) return null
    return {
        CompositionLocalProvider(LocalTextStyle provides textStyle, content = content)
    }
}
