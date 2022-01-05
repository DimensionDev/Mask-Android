package com.dimension.maskbook.wallet.ui.scenes

import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.app.AppScene
import com.dimension.maskbook.wallet.ui.scenes.persona.PersonaScreen
import com.dimension.maskbook.wallet.ui.scenes.settings.SettingsScene
import com.dimension.maskbook.wallet.ui.scenes.wallets.intro.WalletIntroHost
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

private sealed class Screen(val route: String, val name: String, @DrawableRes val icon: Int) {
    object Personas : Screen("Personas", "Personas", R.drawable.ic_persona)
    object Wallets : Screen("Wallets", "Wallets", R.drawable.ic_wallet)
    object App : Screen("App", "App", R.drawable.ic_app)
    object Settings : Screen("Settings", "Settings", R.drawable.ic_settings)
}

private val items = listOf(
    Screen.Personas,
    Screen.Wallets,
    Screen.App,
    Screen.Settings,
)

@ExperimentalMaterialNavigationApi
@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun MainHost(
    onBack: () -> Unit,
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    MaskTheme {
        MaskScaffold(
            bottomBar = {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colors.surface)
                        .height(56.dp)
                ) {
                    items.forEachIndexed { index, screen ->
                        BottomNavigationItem(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(screen.name)
                            },
                            icon = {
                                Icon(
                                    painterResource(id = screen.icon),
                                    contentDescription = null
                                )
                            },
                            selectedContentColor = MaterialTheme.colors.primary,
                            unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
                        )
                    }
                }
            }
        ) { innerPadding ->
            HorizontalPager(
                contentPadding = innerPadding,
                count = items.size,
                state = pagerState,
            ) {
                when (items[it]) {
                    Screen.App -> AppScene(onBack = onBack)
                    Screen.Personas -> PersonaScreen(onBack = onBack)
                    Screen.Settings -> SettingsScene(onBack = onBack)
                    Screen.Wallets -> WalletIntroHost()
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavigationItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean = true,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = ContentAlpha.medium),
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
) {
    val color = if (selected) {
        selectedContentColor
    } else {
        unselectedContentColor
    }
    CompositionLocalProvider(
        LocalContentColor provides color.copy(alpha = 1f),
        LocalContentAlpha provides color.alpha,
        LocalTextStyle provides MaterialTheme.typography.caption.copy(color = color),
    ) {
        Box(
            modifier
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Tab,
                )
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (selected) {
                    text.invoke()
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(color, shape = CircleShape)
                    )
                } else {
                    icon.invoke()
                }
            }
        }
    }
}
