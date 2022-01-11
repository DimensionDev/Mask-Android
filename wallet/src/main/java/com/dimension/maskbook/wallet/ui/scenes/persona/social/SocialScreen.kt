package com.dimension.maskbook.wallet.ui.scenes.persona.social

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.platform.IPlatformSwitcher
import com.dimension.maskbook.wallet.repository.PlatformType
import com.dimension.maskbook.wallet.repository.SocialData
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ui.widget.MaskListCardItem
import com.dimension.maskbook.wallet.ui.widget.MaskListItem
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.TabScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.persona.social.FacebookSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.PersonaSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.SocialViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.TwitterSocialViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun SocialScreen() {
    val viewModel: SocialViewModel = getViewModel()
    val hasPersona by viewModel.hasPersona.observeAsState(initial = false)
    if (hasPersona) {
        PersonaSocialScreen()
    } else {
        EmptyPersonaSocialScreen(
            onCreatePersona = {
                viewModel.setPersona(it)
            },
        )
    }
}

private sealed class Screen(val route: String, val name: String, val target: PlatformType) {
    object Twitter : Screen("Twitter", androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_persona_social_twitter), PlatformType.Twitter)
    object Facebook : Screen("Facebook", androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_persona_social_facebook), PlatformType.Facebook)
}

private val items = listOf(
    Screen.Twitter,
    Screen.Facebook,
)

@Composable
fun PersonaSocialScreen() {
    val navController = rememberNavController()
    val rootNavController = LocalRootNavController.current
    val viewModel = getViewModel<PersonaSocialViewModel>()
    val currentPersona by viewModel.currentPersonaData.observeAsState(initial = null)
    Column {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val selectedTabIndex =
            currentDestination?.route?.let { route -> items.indexOfFirst { it.route == route } }
                ?: 0
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(color = Color.Unspecified)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = MaterialTheme.colors.background,
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset3(tabPositions[selectedTabIndex])
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(
                                color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                },
                edgePadding = 14.dp,
                divider = { },
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                items.forEachIndexed { _, screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    val platformSwitcher = get<IPlatformSwitcher>()
                    Tab(
                        text = { Text(screen.name) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            platformSwitcher.switchTo(screen.target)
                        },
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                            alpha = ContentAlpha.medium
                        ),
                    )
                }
            }
        }


        NavHost(
            navController,
            startDestination = Screen.Twitter.route,
        ) {
            composable(Screen.Twitter.route) {
                currentPersona?.let { persona ->
                    TwitterSocialScene(
                        onAdd = {
                            rootNavController.navigate("ConnectSocial/${persona.id.encodeUrl()}/${PlatformType.Twitter}")
                        },
                        onDisconnect = {
                            rootNavController.navigate("DisconnectSocial/${persona.id.encodeUrl()}/${PlatformType.Twitter}/${it.id.encodeUrl()}")
                        }
                    )
                }

            }
            composable(Screen.Facebook.route) {
                currentPersona?.let { persona ->
                    FacebookSocialScene(
                        onAdd = {
                            rootNavController.navigate("ConnectSocial/${persona.id.encodeUrl()}/${PlatformType.Facebook}")
                        },
                        onDisconnect = {
                            rootNavController.navigate("DisconnectSocial/${persona.id.encodeUrl()}/${PlatformType.Facebook}/${it.id.encodeUrl()}")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TwitterSocialScene(
    onAdd: () -> Unit,
    onDisconnect: (SocialData) -> Unit,
) {
    val viewModel: TwitterSocialViewModel = getViewModel()
    val items by viewModel.items.observeAsState(initial = emptyList())
    LazyColumn {
        if (items.isEmpty()) {
            item {
                MaskListCardItem(
                    modifier = Modifier
                        .clickable {
                            onAdd.invoke()
                        },
                    text = {
                        Text(text = "Connect to Twitter")
                    },
                    icon = {
                        Image(
                            painterResource(id = R.drawable.ic_twitter),
                            contentDescription = null
                        )
                    },
                    trailing = {
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                )
            }
        } else {
            item {
                MaskListItem(
                    text = {
                        Text("Accounts")
                    },
                    trailing = {
                        IconButton(onClick = onAdd) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                )
            }
            items(items) {
                MaskListCardItem(
                    text = {
                        Text(text = it.name)
                    },
                    icon = {
                        Image(
                            painterResource(id = R.drawable.ic_twitter),
                            contentDescription = null
                        )
                    },
                    trailing = {
                        Text(
                            modifier = Modifier.clickable {
                                onDisconnect.invoke(it)
                            },
                            text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_connect_disconnect),
                            color = MaterialTheme.colors.primary,
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FacebookSocialScene(
    onAdd: () -> Unit,
    onDisconnect: (SocialData) -> Unit,
) {
    val viewModel: FacebookSocialViewModel = getViewModel()
    val items by viewModel.items.observeAsState(initial = emptyList())
    LazyColumn {
        if (items.isEmpty()) {
            item {
                MaskListCardItem(
                    modifier = Modifier
                        .clickable {
                            onAdd.invoke()
                        },
                    text = {
                        Text(text = "Connect to Facebook")
                    },
                    icon = {
                        Image(
                            painterResource(id = R.drawable.ic_twitter),
                            contentDescription = null
                        )
                    },
                    trailing = {
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                )
            }
        } else {
            item {
                MaskListItem(
                    text = {
                        Text("Accounts")
                    },
                    trailing = {
                        IconButton(onClick = onAdd) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                )
            }
            items(items) {
                MaskListCardItem(
                    text = {
                        Text(text = it.name)
                    },
                    icon = {
                        Image(
                            painterResource(id = R.drawable.ic_twitter),
                            contentDescription = null
                        )
                    },
                    trailing = {
                        Text(
                            modifier = Modifier.clickable {
                                onDisconnect.invoke(it)
                            },
                            text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_connect_disconnect),
                            color = MaterialTheme.colors.primary,
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun EmptyPersonaSocialScreen(
    onCreatePersona: (String) -> Unit,
) {
    var name by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(TabScaffoldPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painterResource(id = R.drawable.ic_group_81),
            contentDescription = null,
            modifier = Modifier.weight(1f)
        )
        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_persona_welcome_persona), modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onCreatePersona.invoke(name) },
        ) {
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_personas_create_create_persona))
        }
    }
}


fun Modifier.tabIndicatorOffset3(
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