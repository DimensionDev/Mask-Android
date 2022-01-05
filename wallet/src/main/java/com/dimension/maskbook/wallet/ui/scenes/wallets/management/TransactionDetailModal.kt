package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.wallet.repository.TransactionData
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun TransactionDetailModal(
    transactionData: TransactionData,
) {
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding)
        ) {
            Text(
                text = transactionData.message,
                style = MaterialTheme.typography.subtitle1.copy(fontSize = 18.sp),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}