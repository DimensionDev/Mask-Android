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
package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.offDrawableRes
import com.dimension.maskbook.common.ext.onDrawableRes
import com.dimension.maskbook.common.ext.primaryColor
import com.dimension.maskbook.common.ui.widget.CircleCheckbox
import com.dimension.maskbook.common.ui.widget.HorizontalScenePadding
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.MiddleEllipsisText
import com.dimension.maskbook.common.ui.widget.NameImage
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.common.ui.widget.button.MaskTextButton
import com.dimension.maskbook.common.ui.widget.color
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.WalletData

val supportedChainType = buildList {
    add(ChainType.eth)
    add(ChainType.bsc)
    add(ChainType.polygon)
    add(ChainType.arbitrum)
    add(ChainType.xdai)
}

@Composable
fun WalletSwitchSceneModal(
    selectedWallet: WalletData?,
    wallets: List<WalletData>,
    onWalletSelected: (WalletData) -> Unit,
    selectedChainType: ChainType,
    onChainTypeSelected: (ChainType) -> Unit,
    onAddWalletClicked: () -> Unit,
    onWalletConnectClicked: () -> Unit,
    onEditMenuClicked: (data: WalletData) -> Unit,
) {
    var editMode by rememberSaveable {
        mutableStateOf(false)
    }
    MaskModal(
        contentPadding = PaddingValues(0.dp)
    ) {
        MaskScaffold(
            modifier = Modifier.fillMaxHeight(0.8f),
            topBar = {
                Column(
                    modifier = Modifier
                        .background(color = MaterialTheme.colors.background),
                ) {
                    MaskSingleLineTopAppBar(
                        title = {
                            Text(text = stringResource(R.string.scene_wallet_list_title))
                        },
                        actions = {
                            MaskTextButton(
                                onClick = {
                                    editMode = !editMode
                                },
                            ) {
                                Text(
                                    text = if (editMode) {
                                        stringResource(R.string.common_controls_done)
                                    } else {
                                        stringResource(R.string.scene_sendTransaction_sendConfirmPop_edit)
                                    },
                                    color = MaterialTheme.colors.primary,
                                )
                            }
                        },
                    )
                    SupportChainTypeList(
                        selectedChainType = selectedChainType,
                        onChainTypeSelected = onChainTypeSelected,
                    )
                }
            },
            bottomBar = {
                WalletSwitchBottomBar(
                    onClick = onAddWalletClicked,
                    enabled = !editMode,
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(
                    horizontal = HorizontalScenePadding,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(wallets) { wallet ->
                    val isSelected = selectedWallet == wallet
                    WalletSwitchListItem(
                        onClick = {
                            if (!isSelected) {
                                onWalletSelected(wallet)
                            }
                        },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = wallet.name)
                                if (wallet.imported) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.scene_wallet_list_wallets_list_import),
                                        color = MaterialTheme.colors.primary,
                                        style = MaterialTheme.typography.caption,
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colors.primary.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp),
                                            )
                                            .padding(horizontal = 8.dp),
                                    )
                                }
                            }
                        },
                        secondaryText = {
                            MiddleEllipsisText(
                                text = wallet.address,
                                modifier = Modifier.fillMaxWidth(0.6f),
                            )
                        },
                        icon = {
                            NameImage(
                                name = wallet.name,
                                modifier = Modifier.size(32.dp),
                                alpha = 1f,
                                color = wallet.name.color
                            )
                        },
                        trailing = {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                when {
                                    editMode -> {
                                        MaskIconButton(
                                            onClick = {
                                                onEditMenuClicked(wallet)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreHoriz,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                    isSelected -> {
                                        CircleCheckbox(checked = true)
                                    }
                                }
                            }
                        }
                    )
                }
                item {
                    WalletSwitchListItem(
                        enabled = !editMode,
                        onClick = onWalletConnectClicked,
                        text = {
                            Text(text = stringResource(R.string.scene_wallet_connect_wallet_connect))
                        },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.mask1),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                            )
                        },
                        trailing = {
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SupportChainTypeList(
    selectedChainType: ChainType,
    onChainTypeSelected: (ChainType) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = HorizontalScenePadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(supportedChainType) { item ->
            val isSelected = selectedChainType === item
            MaskButton(
                enabled = !isSelected,
                onClick = { onChainTypeSelected(item) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    disabledBackgroundColor = Color.Transparent,
                )
            ) {
                Column(
                    modifier = Modifier.width(46.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (!isSelected) {
                        Spacer(Modifier.height(7.dp))
                    }
                    val res = if (isSelected) item.onDrawableRes else item.offDrawableRes
                    val size = if (isSelected) 46.dp else 32.dp
                    if (res != -1) {
                        Image(
                            painter = painterResource(id = res),
                            contentDescription = null,
                            modifier = Modifier.size(size),
                        )
                    } else {
                        Spacer(
                            modifier = Modifier.size(size),
                        )
                    }
                    if (isSelected) {
                        Spacer(Modifier.height(8.dp))
                        Spacer(
                            Modifier.size(6.dp).background(item.primaryColor, shape = CircleShape)
                        )
                        Spacer(Modifier.height(12.dp))
                    } else {
                        Spacer(Modifier.height(26.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletSwitchListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    MaskButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            disabledBackgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
        ),
    ) {
        MaskListItem(
            icon = icon,
            secondaryText = secondaryText,
            overlineText = overlineText,
            trailing = trailing,
            text = text,
        )
    }
}

@Composable
private fun WalletSwitchBottomBar(
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Column(Modifier.padding(horizontal = HorizontalScenePadding, vertical = 20.dp)) {
        MaskButton(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent,
            )
        ) {
            MaskListItem(
                text = {
                    Text(text = stringResource(R.string.scene_wallet_list_wallets_items_add))
                },
                trailing = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                }
            )
        }
        Spacer(Modifier.height(15.dp))
    }
}
