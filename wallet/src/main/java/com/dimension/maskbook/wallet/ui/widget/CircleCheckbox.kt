package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.dimension.maskbook.wallet.ui.MaskTheme
import kotlin.math.floor
import kotlin.math.max

@Composable
fun CircleCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = CircleCheckboxDefaults.checkedColor,
        checkmarkColor = MaterialTheme.colors.surface,
    )
) {
    val value = ToggleableState(checked)

    val checkCache = remember { CheckDrawingCache() }
    val checkColor by colors.checkmarkColor(value)
    val boxColor by colors.boxColor(enabled = true, value)
    val borderColor by colors.borderColor(enabled = true, value)

    Canvas(
        modifier = modifier
            // .then(toggleableModifier)
            .padding(CheckboxDefaultPadding)
            .wrapContentSize(Alignment.Center)
            .requiredSize(CheckboxSize)
    ) {
        val strokeWidthPx = floor(StrokeWidth.toPx())
        drawCircle(
            boxColor = boxColor,
            borderColor = borderColor,
            strokeWidth = strokeWidthPx
        )
        drawCheck(
            checkColor = checkColor,
            checkFraction = 1f,
            crossCenterGravitation = 0f,
            strokeWidthPx = strokeWidthPx,
            drawingCache = checkCache
        )
    }
}

private fun DrawScope.drawCircle(
    boxColor: Color,
    borderColor: Color,
    strokeWidth: Float
) {
    val halfStrokeWidth = strokeWidth / 2.0f
    val stroke = Stroke(strokeWidth)
    val checkboxSize = size.width
    val radius = checkboxSize / 2

    if (boxColor == borderColor) {
        drawRoundRect(
            boxColor,
            size = Size(checkboxSize, checkboxSize),
            cornerRadius = CornerRadius(radius),
            style = Fill
        )
    } else {
        drawRoundRect(
            boxColor,
            topLeft = Offset(strokeWidth, strokeWidth),
            size = Size(checkboxSize - strokeWidth * 2, checkboxSize - strokeWidth * 2),
            cornerRadius = CornerRadius(max(0f, radius - strokeWidth)),
            style = Fill
        )
        drawRoundRect(
            borderColor,
            topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
            size = Size(checkboxSize - strokeWidth, checkboxSize - strokeWidth),
            cornerRadius = CornerRadius(radius - halfStrokeWidth),
            style = stroke
        )
    }
}

private fun DrawScope.drawCheck(
    checkColor: Color,
    checkFraction: Float,
    crossCenterGravitation: Float,
    strokeWidthPx: Float,
    drawingCache: CheckDrawingCache
) {
    val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Square)
    val width = size.width
    val checkCrossX = 0.45f
    val checkCrossY = 0.65f
    val leftX = 0.3f
    val leftY = 0.5f
    val rightX = 0.7f
    val rightY = 0.35f

    val gravitatedCrossX = lerp(checkCrossX, 0.5f, crossCenterGravitation)
    val gravitatedCrossY = lerp(checkCrossY, 0.5f, crossCenterGravitation)
    // gravitate only Y for end to achieve center line
    val gravitatedLeftY = lerp(leftY, 0.5f, crossCenterGravitation)
    val gravitatedRightY = lerp(rightY, 0.5f, crossCenterGravitation)

    with(drawingCache) {
        checkPath.reset()
        checkPath.moveTo(width * leftX, width * gravitatedLeftY)
        checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
        checkPath.lineTo(width * rightX, width * gravitatedRightY)
        // TODO: replace with proper declarative non-android alternative when ready (b/158188351)
        pathMeasure.setPath(checkPath, false)
        pathToDraw.reset()
        pathMeasure.getSegment(
            0f, pathMeasure.length * checkFraction, pathToDraw, true
        )
    }
    drawPath(drawingCache.pathToDraw, checkColor, style = stroke)
}

@Immutable
private class CheckDrawingCache(
    val checkPath: Path = Path(),
    val pathMeasure: PathMeasure = PathMeasure(),
    val pathToDraw: Path = Path()
)

private val CheckboxDefaultPadding = 2.dp
private val CheckboxSize = 20.dp

private val StrokeWidth = 2.dp

private object CircleCheckboxDefaults {
    val checkedColor = Color(0xFF1FB885)
}

@Preview
@Composable
private fun CircleCheckboxPreview() {
    Column {
        MaskTheme(isDarkTheme = false) {
            Row(Modifier.background(MaterialTheme.colors.background)) {
                CircleCheckbox(true)
                Spacer(Modifier.width(8.dp))
                CircleCheckbox(false)
            }
        }
        MaskTheme(isDarkTheme = true) {
            Row(Modifier.background(MaterialTheme.colors.background)) {
                CircleCheckbox(true)
                Spacer(Modifier.width(8.dp))
                CircleCheckbox(false)
            }
        }
    }
}
