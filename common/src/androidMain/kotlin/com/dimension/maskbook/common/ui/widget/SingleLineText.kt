/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.common.ui.widget

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.InternalFoundationTextApi
import androidx.compose.foundation.text.TextDelegate
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontLoader
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun SingleLineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    suggestedFontSizes: List<TextUnit> = emptyList(),
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    expectSize: TextUnit? = LocalTextStyle.current.fontSize,
) {
    AutoSizeText(
        text = AnnotatedString(text),
        modifier = modifier,
        color = color,
        suggestedFontSizes = suggestedFontSizes,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = 1,
        onTextLayout = onTextLayout,
        style = style,
        expectSize = expectSize
    )
}

@Composable
fun AutoSizeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    suggestedFontSizes: List<TextUnit> = emptyList(),
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    expectSize: TextUnit? = null,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {

        val localStyle = LocalTextStyle.current
        val density = LocalDensity.current
        val resourceLoader = LocalFontLoader.current
        val layoutDirection = LocalLayoutDirection.current

        val combinedTextStyle = remember(localStyle, style) {

            var innerStyle = (localStyle + style).copy(
                fontSize = expectSize ?: minOf(maxWidth, maxHeight).value.sp
            )

            val fontSizes = suggestedFontSizes.ifEmpty {
                MutableList(innerStyle.fontSize.value.toInt()) {
                    (innerStyle.fontSize.value - it).sp
                }
            }

            var currentFontIndex = 0
            while (
                shouldShrink(
                    text = text,
                    textStyle = innerStyle,
                    maxLines = maxLines,
                    constraints = constraints,
                    resourceLoader = resourceLoader,
                    layoutDirection = layoutDirection,
                    density = density,
                ) &&
                ++currentFontIndex < fontSizes.size
            ) {
                innerStyle =
                    innerStyle.copy(fontSize = fontSizes[currentFontIndex])
            }

            innerStyle
        }

        Text(
            text,
            Modifier,
            color,
            TextUnit.Unspecified,
            fontStyle,
            fontWeight,
            fontFamily,
            letterSpacing,
            textDecoration,
            textAlign,
            lineHeight,
            overflow,
            softWrap,
            maxLines,
            inlineContent,
            onTextLayout,
            combinedTextStyle,
        )
    }
}

@OptIn(InternalFoundationTextApi::class)
private fun shouldShrink(
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    density: Density,
    resourceLoader: Font.ResourceLoader,
    layoutDirection: LayoutDirection,
    constraints: Constraints
): Boolean {
    val textDelegate = TextDelegate(
        text,
        textStyle,
        maxLines,
        true,
        TextOverflow.Clip,
        density,
        resourceLoader,
    )

    val textLayoutResult = textDelegate.layout(
        constraints,
        layoutDirection,
    )

    return textLayoutResult.hasVisualOverflow
}
