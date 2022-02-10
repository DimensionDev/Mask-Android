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
package com.dimension.maskbook.wallet.walletconnect.v1

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.ScanQrcodeScene
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.walletconnect.WCClientMeta
import com.dimension.maskbook.wallet.walletconnect.WalletConnectServerManager
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import org.koin.androidx.compose.get

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WalletConnectServerTestScene() {
    val navController = rememberAnimatedNavController()
    var wcuri by remember { mutableStateOf("false") }
    val manager = get<WalletConnectServerManager>()
    var waitingApprove by remember { mutableStateOf(false) }
    var clientMeta by remember { mutableStateOf<WCClientMeta?>(null) }
    val connectedClient by manager.connectedClients.observeAsState(emptyList())
    AnimatedNavHost(
        navController = navController,
        startDestination = "Connect",
        route = "WcServerTest",
    ) {
        composable(
            route = "Scan",
        ) {
            ScanQrcodeScene(
                onBack = {},
                onResult = {
                    wcuri = it
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = "Connect"
        ) {
            MaskScaffold(
                topBar = {
                    MaskSingleLineTopAppBar()
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ScaffoldPadding),
                ) {
                    Text(wcuri)
                    Spacer(Modifier.height(16.dp))
                    if (wcuri.isNotEmpty()) {
                        Button(onClick = {
                            manager.connectClient(wcuri, onRequest = {
                                waitingApprove = true
                                clientMeta = it
                            })
                        }) {
                            Text("try connect")
                        }
                    }

                    Button(onClick = {
                        navController.navigate("Scan")
                    }) {
                        Text("Scan QrCode")
                    }

                    if (clientMeta != null && waitingApprove) {
                        Row {
                            Button(onClick = {
                                manager.rejectConnect(clientMeta!!)
                                clientMeta = null
                                waitingApprove = false
                            }) {
                                Text("reject")
                            }
                            Button(onClick = {
                                manager.approveConnect(
                                    clientMeta!!,
                                    accounts = listOf("0xf479d7007478DC637217Ee0c83E523E09DC2C490"),
                                    chainId = 1
                                )
                                clientMeta = null
                                waitingApprove = false
                            }) {
                                Text("approve")
                            }
                        }
                    }

                    LazyColumn {
                        items(connectedClient) {
                            Row {
                                Text(it.name)
                                Spacer(Modifier.width(16.dp))
                                Button(onClick = {
                                    manager.rejectConnect(it)
                                }) {
                                    Text("disconnect")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
