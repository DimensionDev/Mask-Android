/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
            Row(Modifier.fillMaxWidth()) {
                val subSize = rowSize - row.size
                for (i in row.indices) {
                    val item = row[i]
                    Box(modifier = Modifier.weight(1f)) {
                        itemContent(data.indexOf(item), item)
                    }
                    if (i != row.lastIndex && subSize == 0) {
                        Spacer(modifier = Modifier.width(spacing))
                    }
                }

                if (subSize > 0) {
                    for (j in 0 until subSize) {
                        Spacer(modifier = Modifier.width(spacing))
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (index != rows.lastIndex) {
                Spacer(modifier = Modifier.height(spacing))
            }
        }
    }
}
