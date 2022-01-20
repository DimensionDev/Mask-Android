package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.Dialog
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskIconButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletDerivationPathViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

typealias DerivationPathItem = ImportWalletDerivationPathViewModel.BalanceRow

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImportWalletDerivationPathScene(
    onBack: () -> Unit,
    onDone: (WalletCreateOrImportResult) -> Unit,
    wallet: String,
    code: List<String>,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                )
            }
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ScaffoldPadding),
                ) {
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

                    Text(text = stringResource(R.string.scene_wallet_derivation_path_title), style = MaterialTheme.typography.h4)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = path)

                    Spacer(modifier = Modifier.height(25.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.scene_wallet_derivation_path_header_address),
                            modifier = Modifier.weight(1.5f)
                        )
                        Text(
                            text = stringResource(R.string.scene_wallet_derivation_path_header_balance),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = stringResource(R.string.scene_wallet_derivation_path_header_operation),
                            modifier = Modifier.weight(1f),
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
                            onCheckClicked = { item ->
                                viewModel.switchStatus(item.path)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))
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
                                if (it.type == WalletCreateOrImportResult.Type.SUCCESS) onDone.invoke(
                                    it.copy()
                                )
                                result = null
                            })
                        }
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
    onCheckClicked: (DerivationPathItem) -> Unit,
) {
    if (list.isEmpty()) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(list) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ImportWalletDerivationPathDefaults.DerivationPathItemHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.address,
                    modifier = Modifier.weight(1.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = balances[item.address] ?: "--",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                )
                if (item.isAdded) {
                    Text(
                        text = ImportWalletDerivationPathDefaults.DerivationPathItemIsAdded,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onCheckClicked(item) },
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Checkbox(
                            checked = isChecked(item),
                            onCheckedChange = null,
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
    val DerivationPathItemHeight = 35.dp
    const val DerivationPathItemIsAdded = "Is Added"
    val SwitcherIconSize = 20.dp
    const val SwitcherSelectLeft = "select left"
    const val SwitcherSelectRight = "select right"
    val SwitcherIconEnableColor = Color(0xFF6B738D)
    val SwitcherIconDisEnableColor = Color(0xFFCBD1D9)
}
