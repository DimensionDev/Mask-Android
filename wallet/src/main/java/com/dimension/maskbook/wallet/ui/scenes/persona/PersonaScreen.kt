package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.persona.contacts.ContactsScene
import com.dimension.maskbook.wallet.ui.scenes.persona.post.PostScene
import com.dimension.maskbook.wallet.ui.scenes.persona.social.SocialScreen
import com.dimension.maskbook.wallet.ui.widget.MaskCard
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.viewmodel.persona.PersonaViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import org.koin.androidx.compose.getViewModel

private sealed class Screen(val route: String, val name: String) {
    object Social : Screen("Social", androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_personas_tabs_social))
    object Post : Screen("Post", androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_personas_tabs_post))
    object Contacts : Screen("Contacts", androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_backup_restored_contacts))
}

private val items = listOf(
    Screen.Social,
//    Screen.Post,
    Screen.Contacts
)

@ExperimentalAnimationApi
@Composable
fun PersonaScreen(
    onBack: () -> Unit,
) {
    val rootNavController = LocalRootNavController.current
    val navController = rememberAnimatedNavController()
    val viewModel: PersonaViewModel = getViewModel()
    val persona by viewModel.persona.observeAsState(initial = null)
    MaskTheme {
        MaskScaffold(
            topBar = {
                Surface(
                    color = MaterialTheme.colors.background,
                    elevation = 16.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(
                            bottom = 8.dp,
                        ),
                    ) {
                        MaskSingleLineTopAppBar(
                            navigationIcon = {
                            },
                            actions = {
                                MaskCard(
                                    modifier = Modifier.aspectRatio(1f)
                                ) {
                                    IconButton(onClick = onBack) {
                                        Image(
                                            painter = painterResource(id = R.drawable.twitter_1),
                                            contentDescription = null,
                                        )
                                    }
                                }
                            },
                            title = {
                                persona?.let { persona ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            rootNavController.navigate("PersonaMenu")
                                        }
                                    ) {
                                        Text(text = persona.name)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            Icons.Default.ChevronLeft,
                                            contentDescription = null,
                                            modifier = Modifier.rotate(-90f)
                                        )
                                    }
                                }
                            }
                        )
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        val selectedTabIndex =
                            currentDestination?.route?.let { route -> items.indexOfFirst { it.route == route } }
                                ?: 0
                        CompositionLocalProvider(
                            LocalTextStyle provides LocalTextStyle.current.copy(color = Color.Unspecified)
                        ) {
                            TabRow(
                                selectedTabIndex = selectedTabIndex,
                                backgroundColor = MaterialTheme.colors.background,
                                divider = {
                                    TabRowDefaults.Divider(thickness = 0.dp)
                                },
                                indicator = { tabPositions ->
                                    Box(
                                        Modifier
                                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                            .height(3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth(0.1f)
                                                .fillMaxHeight()
                                                .background(
                                                    color = MaterialTheme.colors.primary,
                                                    shape = RoundedCornerShape(99.dp)
                                                )
                                        )
                                    }
                                },
                            ) {
                                items.forEachIndexed { _, screen ->
                                    Tab(
                                        text = { Text(screen.name) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        selectedContentColor = MaterialTheme.colors.primary,
                                        unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                                            alpha = ContentAlpha.medium
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = Screen.Social.route,
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.Social.route) {
                    SocialScreen()
                }
                composable(Screen.Post.route) {
                    PostScene()
                }
                composable(Screen.Contacts.route) {
                    ContactsScene()
                }
            }
        }
    }
}

fun Modifier.tabIndicatorOffset2(
    currentTabPosition: TabPosition
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}