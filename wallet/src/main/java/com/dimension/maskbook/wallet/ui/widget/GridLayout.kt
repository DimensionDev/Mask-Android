package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.sqrt

@Composable
fun GridLayout(
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val pxSpacing = with(LocalDensity.current) {
        spacing.roundToPx()
    }
    val measurePolicy = gridLayoutMeasurePolicy(pxSpacing)
    Layout(
        content = { content() },
        measurePolicy = measurePolicy,
        modifier = modifier
    )
}

@Composable
private fun gridLayoutMeasurePolicy(
    spacing: Int,
) = remember(spacing) {
    MeasurePolicy { measurables, constraints ->
        val columns = ceil(sqrt(measurables.size.toDouble()))
        val rows = ceil((measurables.size.toDouble() / columns))
        val itemWidth =
            ((constraints.maxWidth.toDouble() - spacing * (columns - 1)) / columns).toInt()
        val itemHeight = if (constraints.maxHeight != Constraints.Infinity) {
            ((constraints.maxHeight.toDouble() - spacing * (rows - 1)) / rows).toInt()
        } else {
            itemWidth
        }
        val placeables = measurables.map { measurable ->
            measurable.measure(Constraints.fixed(width = itemWidth, height = itemHeight))
        }

        layout(
            width = constraints.maxWidth,
            height = (itemHeight * rows + spacing * (rows - 1)).toInt()
        ) {
            var currentX = 0
            var currentY = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = currentX, y = currentY)
                currentX += (itemWidth + spacing)
                if (currentX >= constraints.maxWidth) {
                    currentX = 0
                    currentY += (itemHeight + spacing)
                }
            }
        }
    }
}


fun <T> LazyListScope.itemsGridIndexed(
    data: List<T>,
    rowSize: Int,
    spacing: Dp = 0.dp,
    padding: Dp = 0.dp,
    itemContent: @Composable BoxScope.(Int, T) -> Unit,
) {
    val rows = data.windowed(rowSize, rowSize, true)
    itemsIndexed(rows) { index, row ->
        Column(
            modifier = Modifier
                .fillParentMaxWidth()
                .padding(horizontal = padding)
        ) {
            Row {
                for (i in row.indices) {
                    val item = row[i]
                    Box(modifier = Modifier.weight(1f)) {
                        itemContent(data.indexOf(item), item)
                    }
                    if (i != row.lastIndex) {
                        Spacer(modifier = Modifier.width(spacing))
                    }
                }
            }
            if (index != rows.lastIndex) {
                Spacer(modifier = Modifier.height(spacing))
            }
        }
    }
}