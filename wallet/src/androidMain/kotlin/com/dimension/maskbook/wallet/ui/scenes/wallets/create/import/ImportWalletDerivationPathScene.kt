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
package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.MiddleEllipsisText
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.SingleLineText
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.common.ui.widget.button.MaskTextButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.route.WalletRoute
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.Dialog
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletDerivationPathViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

typealias DerivationPathItem = ImportWalletDerivationPathViewModel.BalanceRow

@NavGraphDestination(
    route = WalletRoute.ImportWallet.DerivationPath.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImportWalletDerivationPathScene(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("wallet") wallet: String,
    @Path("mnemonicCode") codeString: String,
) {
    // "word1+word2+word3+word5..."
    val code = codeString.split("+")
    val viewModel = getViewModel<ImportWalletDerivationPathViewModel> {
        parametersOf(wallet, code)
    }
    val path by viewModel.derivationPath.observeAsState(initial = "")
    val checked by viewModel.checked.observeAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<WalletCreateOrImportResult?>(null) }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    fun scrollToPage(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    MaskScene {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = stringResource(R.string.scene_wallet_derivation_path_title))
                    },
                    subTitle = {
                        Text(text = path)
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.scene_wallet_derivation_path_header_address),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = stringResource(R.string.scene_wallet_derivation_path_header_balance, "ETH"),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = stringResource(R.string.scene_wallet_derivation_path_header_operation),
                        modifier = Modifier.weight(0.6f),
                        textAlign = TextAlign.End
                    )
                }
                HorizontalPager(
                    count = Int.MAX_VALUE,
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                ) { page ->
                    val items by viewModel.getPagerItems(page).collectAsState()
                    val balances by viewModel.getBalanceMap(page).collectAsState()
                    DerivationPathPager(
                        list = items,
                        balances = balances,
                        isChecked = { item ->
                            checked.contains(item.path)
                        },
                        onItemClick = { item ->
                            viewModel.switchStatus(item.path)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    PagerSwitcherIcon(
                        painter = painterResource(id = R.drawable.ic_select_left),
                        contentDescription = ImportWalletDerivationPathDefaults.SwitcherSelectRight,
                        enabled = pagerState.currentPage > 0,
                        onClick = {
                            scrollToPage(pagerState.currentPage - 1)
                        },
                    )
                    PagerSwitcherIcon(
                        painter = painterResource(id = R.drawable.ic_select_right),
                        contentDescription = ImportWalletDerivationPathDefaults.SwitcherSelectLeft,
                        enabled = pagerState.currentPage < Int.MAX_VALUE,
                        onClick = {
                            scrollToPage(pagerState.currentPage + 1)
                        },
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.next {
                            result = it
                            showDialog = true
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.common_controls_next))
                }
                if (showDialog) {
                    result?.let {
                        it.Dialog(onDismissRequest = {
                            showDialog = false
                            if (it.type == WalletCreateOrImportResult.Type.SUCCESS) {
                                navController.navigate(
                                    Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Wallet)),
                                    navOptions = navOptions {
                                        launchSingleTop = true
                                        popUpTo(CommonRoute.Main.Home.path) {
                                            inclusive = false
                                        }
                                    }
                                )
                            }
                            result = null
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun DerivationPathPager(
    list: List<DerivationPathItem>,
    balances: Map<String, String>,
    isChecked: (DerivationPathItem) -> Boolean,
    onItemClick: (DerivationPathItem) -> Unit,
) {
    if (list.isEmpty()) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(list) { item ->
            MaskTextButton(
                enabled = !item.isAdded,
                onClick = { onItemClick(item) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ImportWalletDerivationPathDefaults.DerivationPathItemHeight),
                contentPadding = PaddingValues(0.dp)
            ) {
                MiddleEllipsisText(
                    text = item.address,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )
                SingleLineText(
                    text = balances[item.address] ?: "--",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                )
                if (item.isAdded) {
                    Text(
                        text = ImportWalletDerivationPathDefaults.DerivationPathItemIsAdded,
                        modifier = Modifier.weight(0.6f),
                        textAlign = TextAlign.End,
                        color = LocalTextStyle.current.color.copy(ContentAlpha.disabled),
                    )
                } else {
                    Box(
                        modifier = Modifier.weight(0.6f).fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Checkbox(
                            checked = isChecked(item),
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(
                                checkedColor = ImportWalletDerivationPathDefaults.SwitcherBoxCheckedColor
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PagerSwitcherIcon(
    painter: Painter,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    MaskIconButton(enabled = enabled, onClick = onClick) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.size(ImportWalletDerivationPathDefaults.SwitcherIconSize),
            tint = if (enabled) {
                ImportWalletDerivationPathDefaults.SwitcherIconEnableColor
            } else {
                ImportWalletDerivationPathDefaults.SwitcherIconDisEnableColor
            }
        )
    }
}

object ImportWalletDerivationPathDefaults {
    val DerivationPathItemHeight = 40.dp
    const val DerivationPathItemIsAdded = "Added"
    val SwitcherIconSize = 20.dp
    const val SwitcherSelectLeft = "select left"
    const val SwitcherSelectRight = "select right"
    val SwitcherIconEnableColor = Color(0xFF6B738D)
    val SwitcherIconDisEnableColor = Color(0xFFCBD1D9)
    val SwitcherBoxCheckedColor = Color(0xFF1FB885)
    val SwitcherIsAddedTextColor = Color(0xFFB4B8C8)
}
