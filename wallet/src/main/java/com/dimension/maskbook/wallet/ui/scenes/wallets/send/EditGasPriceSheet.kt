package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.GasPriceEditMode
import com.dimension.maskbook.wallet.ui.widget.MaskInputField
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun EditGasPriceSheet(
    price: String,
    costFee: String,
    costFeeUnit: String,// e.g ETH
    arrivesIn: String,// 30 Sec
    mode: GasPriceEditMode,
    gasLimit: String,
    onGasLimitChanged: (String) -> Unit,
    maxPriorityFee: String,
    maxPriorityFeePrice: String,
    onMaxPriorityFeeChanged: (String) -> Unit,
    maxFee: String,
    maxFeePrice: String,
    onMaxFeeChanged: (String) -> Unit,
    onSelectMode: (GasPriceEditMode) -> Unit,
    gasLimitError: String?,
    maxPriorityFeeError: String?,
    maxFeeError: String?,
    canConfirm: Boolean,
    onConfirm: () -> Unit
) {
    MaskModal {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(ScaffoldPadding),
        ) {
            var showAdvanced by remember {
                mutableStateOf(false)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_sendTransaction_gasPrice_title),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "~$price",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${stringResource(R.string.scene_sendTransaction_gasPrice_costFee)}$costFee $costFeeUnit",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = arrivesIn, color = Color(0xFF60DFAB),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                SelectableButton(
                    text = "Low",
                    selected = mode == GasPriceEditMode.LOW,
                    onSelect = { onSelectMode.invoke(GasPriceEditMode.LOW) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                SelectableButton(
                    text = "Medium",
                    selected = mode == GasPriceEditMode.MEDIUM,
                    onSelect = { onSelectMode.invoke(GasPriceEditMode.MEDIUM) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                SelectableButton(
                    text = "High",
                    selected = mode == GasPriceEditMode.HIGH,
                    onSelect = { onSelectMode.invoke(GasPriceEditMode.HIGH) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { showAdvanced = !showAdvanced }) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_sendTransaction_gasPrice_advancedBtn))
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    painter = painterResource(id = if (showAdvanced) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                    contentDescription = null
                )
            }

            CustomContent(
                visible = showAdvanced,
                gasLimit = gasLimit,
                onGasLimitChanged = onGasLimitChanged,
                maxPriorityFee = maxPriorityFee,
                onMaxPriorityFeeChanged = onMaxPriorityFeeChanged,
                maxFee = maxFee,
                onMaxFeeChanged = onMaxFeeChanged,
                maxFeePrice = maxFeePrice,
                maxPriorityFeePrice = maxPriorityFeePrice,
                gasLimitError = gasLimitError,
                maxFeeError = maxFeeError,
                maxPriorityFeeError = maxPriorityFeeError
            )

            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onConfirm,
                enabled = canConfirm
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CustomContent(
    visible: Boolean,
    gasLimit: String,
    onGasLimitChanged: (String) -> Unit,
    maxPriorityFee: String,
    onMaxPriorityFeeChanged: (String) -> Unit,
    maxFee: String,
    onMaxFeeChanged: (String) -> Unit,
    maxPriorityFeePrice: String,
    maxFeePrice: String,
    gasLimitError: String?,
    maxPriorityFeeError: String?,
    maxFeeError: String?
) {
    AnimatedVisibility(visible = visible) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.scene_sendTransaction_gasPrice_gasLimit),
            )
            Spacer(modifier = Modifier.height(8.dp))
            MaskInputField(
                value = gasLimit,
                onValueChange = onGasLimitChanged,
                modifier = Modifier.fillMaxWidth(),
            )
            if (!gasLimitError.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$gasLimitError", color = MaterialTheme.colors.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_sendTransaction_gasPrice_maxPriorityFee),
                    modifier = Modifier.weight(1f)
                )
                Text(text = "~$maxPriorityFeePrice")
            }
            Spacer(modifier = Modifier.height(8.dp))
            MaskInputField(
                value = maxPriorityFee,
                onValueChange = onMaxPriorityFeeChanged,
                modifier = Modifier.fillMaxWidth(),
            )
            if (!maxPriorityFeeError.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$maxPriorityFeeError", color = MaterialTheme.colors.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_sendTransaction_gasPrice_maxFee),
                    modifier = Modifier.weight(1f)
                )
                Text(text = "~$maxFeePrice")
            }
            Spacer(modifier = Modifier.height(8.dp))
            MaskInputField(
                value = maxFee,
                onValueChange = onMaxFeeChanged,
                modifier = Modifier.fillMaxWidth(),
            )
            if (!maxFeeError.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$maxFeeError", color = MaterialTheme.colors.error)
            }
        }
    }
}

@Composable
private fun SelectableButton(
    modifier: Modifier,
    text: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(
                color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(
                    alpha = 0.1f
                ),
                shape = MaterialTheme.shapes.small
            )
            .clip(MaterialTheme.shapes.small)
            .padding(vertical = 21.dp, horizontal = 16.dp)
            .clickable(enabled = !selected, onClick = onSelect)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            color = if (selected) MaterialTheme.colors.onPrimary
            else MaterialTheme.colors.onSurface.copy(
                alpha = ContentAlpha.medium
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}