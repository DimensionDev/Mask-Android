package com.dimension.maskbook.wallet.ui.scenes.wallets.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.*

@Composable
fun LegalScene(
    onBack: () -> Unit,
    onBrowseAgreement: () -> Unit,
    onAccept: () -> Unit,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_terms_of_service_title), style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(R.string.scene_terms_of_service_description))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_legal),
                        contentDescription = null
                    )
                }
                TextButton(onClick = onBrowseAgreement) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_terms_of_service_service_agreement), modifier = Modifier.weight(1F))
                        Icon(
                            imageVector = Icons.Default.ArrowRight,
                            contentDescription = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_terms_of_service_service_agreement)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onAccept()
                    },
                ) {
                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_accept))
                }
            }
        }
    }
}