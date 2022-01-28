package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.CheckboxColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MaskSelection(
    selected: Boolean,
    onClicked: () -> Unit,
    enabled: Boolean = true,
    checkboxColors: CheckboxColors = CircleCheckboxDefaults.colors(),
    content: @Composable RowScope.() -> Unit,
) {
    Card(
        elevation = 0.dp,
        backgroundColor = if (selected) MaterialTheme.colors.surface else Color.Transparent,
        onClick = onClicked,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.h5,
                content = { content.invoke(this) }
            )
            if (selected) {
                Spacer(Modifier.weight(1f))
                CircleCheckbox(
                    checked = true,
                    colors = checkboxColors,
                )
            }
        }
    }
}
