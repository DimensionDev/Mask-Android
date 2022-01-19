package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R

@Composable
fun MaskTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable() (() -> Unit)? = null,
    subTitle: @Composable() (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 0.dp
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        shape = RectangleShape,
        modifier = modifier
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(AppBarDefaults.ContentPadding)
                    .height(AppBarHeight),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (navigationIcon == null) {
                    Spacer(TitleInsetWithoutIcon)
                } else {
                    Row(TitleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                            content = navigationIcon
                        )
                    }
                }

                Spacer(
                    Modifier
                        .weight(1f),
                )

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Row(
                        Modifier
                            .height(40.dp)
                            .padding(end = 22.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        content = actions
                    )
                }
            }
            if (title != null) {
                Row {
                    Spacer(TitleInsetWithoutIcon)
                    ProvideTextStyle(value = MaterialTheme.typography.h1) {
                        title()
                    }
                }
            }
            if (subTitle != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 23.dp)
                ) {
                    subTitle.invoke()
                }
            }
        }
    }
}


@Composable
fun MaskSingleLineTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 0.dp
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        shape = RectangleShape,
        modifier = modifier
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(AppBarHeight),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(AppBarDefaults.ContentPadding),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (navigationIcon == null) {
                    Spacer(TitleInsetWithoutIcon)
                } else {
                    Row(
                        TitleIconModifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                            content = navigationIcon
                        )
                    }
                }
                Spacer(
                    Modifier
                        .weight(1f),
                )

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Row(
                        Modifier
                            .height(40.dp)
                            .padding(end = 22.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        content = actions
                    )
                }
            }
            if (title != null) {
                Row(
                    Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.h4) {
                        title()
                    }
                }
            }
        }
    }
}


private val AppBarHeight = 56.dp
private val AppBarHorizontalPadding = 4.dp
private val TitleInsetWithoutIcon = Modifier.width(23.dp)
private val TitleIconModifier = Modifier
//    .fillMaxHeight()
    .height(40.dp)
    .padding(start = 22.dp)
//    .width(72.dp - AppBarHorizontalPadding)

@Composable
fun MaskBackButton(
    onBack: () -> Unit,
) {
    MaskCard(
        modifier = Modifier.aspectRatio(1f),
    ) {
        IconButton(onClick = { onBack.invoke() }) {
            Icon(painterResource(id = R.drawable.ic_arrow_left), contentDescription = null)
        }
    }
}