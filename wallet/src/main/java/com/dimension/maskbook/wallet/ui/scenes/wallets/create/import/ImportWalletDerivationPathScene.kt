package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.wallets.common.Dialog
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.wallets.import.ImportWalletDerivationPathViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

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
                    val items = viewModel.balancesPager.collectAsLazyPagingItems()
                    val checked by viewModel.checked.observeAsState(initial = emptyList())
                    var showDialog by remember {
                        mutableStateOf(false)
                    }
                    var result by remember {
                        mutableStateOf<WalletCreateOrImportResult?>(null)
                    }
                    Text(text = "Derivation path", style = MaterialTheme.typography.h4)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = path)
                    Spacer(modifier = Modifier.height(25.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(text = "Address", modifier = Modifier.weight(1f))
                                Text(
                                    text = "",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = "Operation",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                        items(items) { item ->
                            item?.let {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = item.address,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.balances.toString(),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End,
                                    )
                                    Box(modifier = Modifier.weight(1f)) {
                                        Checkbox(
                                            checked = checked.contains(item.path),
                                            onCheckedChange = {
                                                viewModel.switchStatus(
                                                    item
                                                )
                                            },
                                            modifier = Modifier.align(Alignment.CenterEnd)
                                        )
                                    }
                                }
                            }
                        }
                    }
//                    Spacer(modifier = Modifier.height(6.dp))
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        IconButton(
//                            onClick = {
//                                scope.launch {
//                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
//                                }
//                            },
//                            enabled = pagerState.currentPage > 0 && tables.isNotEmpty()
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .size(20.dp)
//                                    .background(
//                                        color = MaterialTheme.colors.onSurface.copy(alpha = LocalContentAlpha.current),
//                                        shape = CircleShape
//                                    ),
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.ArrowLeft,
//                                    contentDescription = null,
//                                    tint = MaterialTheme.colors.surface
//                                )
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(12.dp))
//                        IconButton(
//                            onClick = {
//                                scope.launch {
//                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
//                                }
//                            },
//                            enabled = pagerState.currentPage < tables.size - 1 && tables.isNotEmpty()
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .size(20.dp)
//                                    .background(
//                                        color = MaterialTheme.colors.onSurface.copy(alpha = LocalContentAlpha.current),
//                                        shape = CircleShape
//                                    ),
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.ArrowRight,
//                                    contentDescription = null,
//                                    tint = MaterialTheme.colors.surface
//                                )
//                            }
//                        }
//                    }
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
                        Text(text = "Next")
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