package com.dimension.maskbook.wallet.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.Appearance
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import org.koin.androidx.compose.get

@Composable
fun MaskTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = provideColor(),
        shapes = provideShapes(),
        typography = provideTypography(),
        content = content,
    )
}

@Composable
fun provideTypography(): Typography {
    return Typography().let {
        it.copy(
            body1 = it.body1.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF6B738D)
                },
            ),
            body2 = it.body2.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF6B738D)
                },
            ),
            h1 = it.h1.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
            h2 = it.h2.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
            h3 = it.h3.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
            h4 = it.h4.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
            h5 = it.h5.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
            h6 = it.h6.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
            subtitle1 = it.subtitle1.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
            subtitle2 = it.subtitle2.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
            button = it.button.copy(
                color = if (isDarkTheme()) {
                    Color.Unspecified
//                    Color(0XCCFFFFFF)
                } else {
                    Color.Unspecified
//                    Color(0XFF1D2238)
                }
            ),
            caption = it.caption.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
            overline = it.overline.copy(
                color = if (isDarkTheme()) {
                    Color(0XCCFFFFFF)
                } else {
                    Color(0XFF1D2238)
                }
            ),
        )
    }
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
private fun isDarkTheme(): Boolean {
    val repo = get<ISettingsRepository>()
    val appearance by repo.appearance.observeAsState(initial = Appearance.default)
    return when (appearance) {
        Appearance.default -> isSystemInDarkTheme()
        Appearance.light -> false
        Appearance.dark -> true
    }
}

@Composable
fun provideColor(): Colors {
    val primary = Color(0xFF1C68F3)
    return if (isDarkTheme()) {
        darkColors(
            primary = primary,
            secondary = primary,
            background = Color(0XFF050919),
            surface = Color(0XFF171C31),
            secondaryVariant = primary,
        )
    } else {
        lightColors(
            primary = primary,
            secondary = primary,
            background = Color(0XFFF6F8FB),
            secondaryVariant = primary,
        )
    }
}
