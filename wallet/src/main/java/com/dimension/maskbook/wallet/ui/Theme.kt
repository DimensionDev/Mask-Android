package com.dimension.maskbook.wallet.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.Appearance
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import org.koin.androidx.compose.get

@Composable
fun MaskTheme(
    isDarkTheme: Boolean = isDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = provideColor(isDarkTheme),
        shapes = provideShapes(),
        typography = provideTypography(isDarkTheme),
        content = content,
    )
}

@Composable
fun provideTypography(isDarkTheme: Boolean): Typography {
    return Typography(
        h1 = TextStyle(
            fontSize = 32.sp,
            lineHeight = 38.4.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.W700,
            color = if (isDarkTheme) Color.White.copy(0.8f) else Color(0xFF1D2238)
        ),
        h3 = TextStyle(
            fontSize = 20.sp,
            lineHeight = 30.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.W700,
            color = if (isDarkTheme) Color.White.copy(0.8f) else Color(0xFF1D2238)
        ),
        h4 = TextStyle(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.W700,
            color = if (isDarkTheme) Color.White.copy(0.8f) else Color(0xFF1D2238),
        ),
        h5 = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.W700,
            color = if (isDarkTheme) Color.White.copy(0.8f) else Color(0xFF1D2238),
        ),
        body1 = TextStyle(
            fontSize = 16.sp,
            lineHeight = 21.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight(510),
            color = if (isDarkTheme) Color.White.copy(0.4f) else Color(0xFF6B738D),
        ),
        body2 = TextStyle(
            fontSize = 13.sp,
            lineHeight = 19.5.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.W400,
            color = if (isDarkTheme) Color.White.copy(0.4f) else Color(0xFF6B738D),
        ),
        button = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.W700,
            color = Color.Unspecified,
            textAlign = TextAlign.Center,
        )
    )
}

@Composable
fun provideShapes(): Shapes {
    return Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(20.dp),
        large = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    )
}


@Composable
fun isDarkTheme(): Boolean {
    val repo = get<ISettingsRepository>()
    val appearance by repo.appearance.observeAsState(initial = Appearance.default)
    return when (appearance) {
        Appearance.default -> isSystemInDarkTheme()
        Appearance.light -> false
        Appearance.dark -> true
    }
}

@Composable
fun provideColor(isDarkTheme: Boolean): Colors {
    val primary = Color(0xFF1C68F3)
    return if (isDarkTheme) {
        darkColors(
            primary = primary,
            secondary = primary,
            background = Color(0XFF050919),
            secondaryVariant = primary,
            surface = Color(0XFF171C31),
            onSurface = Color.White.copy(alpha = 0.4f),
        )
    } else {
        lightColors(
            primary = primary,
            secondary = primary,
            background = Color(0XFFF6F8FB),
            secondaryVariant = primary,
            surface = Color.White,
            onSurface = Color(0xFF6B738D),
        )
    }
}
