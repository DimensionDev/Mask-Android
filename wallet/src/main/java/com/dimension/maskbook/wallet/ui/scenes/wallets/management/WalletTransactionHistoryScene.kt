package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.TransactionData
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.wallets.token.TransactionHistoryList
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar

@Composable
fun WalletTransactionHistoryScene(
    onBack: () -> Unit,
    transactions: List<TransactionData>,
    onSpeedUp: (TransactionData) -> Unit,
    onCancel: (TransactionData) -> Unit,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.scene_wallet_detail_wallet_items_history))
                    },
                    navigationIcon = {
                        MaskBackButton(
                            onBack = onBack
                        )
                    }
                )
            }
        ) {
            TransactionHistoryList(
                transactions = transactions,
                onSpeedUp = onSpeedUp,
                onCancel = onCancel,
            )
        }
    }
}