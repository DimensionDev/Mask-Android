package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun IosSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = Color.Transparent,
        checkedTrackAlpha = 0f,
        uncheckedThumbColor = Color.White,
        uncheckedTrackColor = Color.Transparent,
        uncheckedTrackAlpha = 0f,
    )
) {
    val background by animateColorAsState(
        if (checked) {
            IosSwitchDefault.checkedColor
        } else {
            IosSwitchDefault.unCheckedColor
        }
    )
    Box(
        modifier = Modifier
            .background(color = background, shape = CircleShape)
            .size(
                width = IosSwitchDefault.TrackWidth,
                height = IosSwitchDefault.TrackHeight
            ),
        contentAlignment = Alignment.Center,
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled,
            interactionSource = interactionSource,
            colors = colors,
        )
    }
}

private object IosSwitchDefault {
    val TrackWidth = 44.dp
    val TrackHeight = 28.dp
    val checkedColor = Color(0xFF34C759)
    val unCheckedColor = Color(0xFFCBD1D9)
}

@Preview(showBackground = true)
@Composable
private fun IosSwitchPreview() {
    Column {
        Row {
            IosSwitch(
                checked = true,
                onCheckedChange = null,
            )
            Spacer(Modifier.width(10.dp))
            IosSwitch(
                checked = false,
                onCheckedChange = null
            )
        }
        Spacer(Modifier.height(10.dp))
        MaskListItem(
            text = {
                Text("Title")
            },
            trailing = {
                IosSwitch(
                    checked = true,
                    onCheckedChange = null,
                )
            }
        )
    }
}
