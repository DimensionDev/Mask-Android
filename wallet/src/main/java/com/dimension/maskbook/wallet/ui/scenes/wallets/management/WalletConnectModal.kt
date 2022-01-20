package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

enum class WalletConnectType {
    Manually,
    QRCode,
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WalletConnectModal() {
    val navController = rememberAnimatedNavController()
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding),
        ) {
            Text(
                text = stringResource(R.string.scene_wallet_connect_wallet_connect),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            AnimatedNavHost(
                navController = navController,
                startDestination = "WalletConnectTypeSelect"
            ) {
                composable("WalletConnectTypeSelect") {
                    TypeSelectScene()
                }

                composable("WalletConnectConnecting") {
                    Connecting()
                }

                composable("WalletConnectFailed") {
                    WalletConnectFailure(
                        onRetry = {

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WalletConnectFailure(
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFFF5F5F)),
            contentAlignment = Alignment.Center,
        ) {
            Image(painterResource(id = R.drawable.ic_close_square), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = stringResource(R.string.scene_wallet_connect_connection_fail), color = Color(0xFFFF5F5F))
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(onClick = onRetry) {
            Text(text = stringResource(R.string.common_controls_try_again))
        }
    }
}

@Composable
fun Connecting() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(id = R.drawable.ic_mask1), contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(26.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(painterResource(id = R.drawable.mask1), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = stringResource(R.string.scene_wallet_connect_connecting))
    }
}


@Composable
private fun TypeSelectScene() {
    Column {
        var selectedTabIndex by remember {
            mutableStateOf(0)
        }
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
                                shape = CircleShape,
                            )
                    )
                }
            },
        ) {
            WalletConnectType.values().forEachIndexed { index, type ->
                Tab(
                    text = { Text(type.name) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                    },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                        alpha = ContentAlpha.medium
                    ),
                )
            }
        }
        when (WalletConnectType.values()[selectedTabIndex]) {
            WalletConnectType.Manually -> WalletConnectManually()
            WalletConnectType.QRCode -> WalletConnectQRCode()
        }
    }
}

@Composable
fun WalletConnectQRCode() {
    Text(text = stringResource(R.string.scene_wallet_connect_qr_code_tips))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
    ) {
        Icon(
            Icons.Default.Home,
            contentDescription = null,
        )// TODO: Display qr code

    }
    Text(text = stringResource(R.string.scene_wallet_connect_tap_to_copy))// TODO: Copy
}

@Composable
fun WalletConnectManually() {
    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = MaterialTheme.colors.background,
        indicator = { tabPositions ->
            Box(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small,
                    )
            )
        },
        edgePadding = 14.dp,
        divider = { },
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        supportedChainType.forEachIndexed { index, type ->
            val selected = selectedTabIndex == index
            Tab(
                text = { Text(type.name) },
                selected = selected,
                onClick = {
                    selectedTabIndex = index
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                    alpha = ContentAlpha.medium
                ),
            )
        }

    }
}
