package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ui.widget.MaskInputField
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun AddContactSheet(
    avatarLabel: String,
    address: String,
    canConfirm: Boolean,
    nameInput: String,
    onNameChanged: (String) -> Unit,
    onAddContact: () -> Unit
) {
    MaskModal {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(ScaffoldPadding),
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colors.primary)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = avatarLabel,
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            MaskInputField(
                value = nameInput,
                onValueChange = onNameChanged,
                placeholder = { Text(text = "Name") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = address,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAddContact,
                enabled = canConfirm
            ) {
                Text(text = "Add Contact")
            }
        }
    }
}