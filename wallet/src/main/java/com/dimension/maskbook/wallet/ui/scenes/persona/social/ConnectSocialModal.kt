package com.dimension.maskbook.wallet.ui.scenes.persona.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.repository.SocialData
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConnectSocialModal(
    socials: List<SocialData>,
    onConnect: (SocialData) -> Unit,
) {
    var selectedSocial by remember {
        mutableStateOf<SocialData?>(null)
    }
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Select Username", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn {
                items(socials) {
                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        val backgroundColor = if (selectedSocial == it) {
                            MaterialTheme.colors.primary
                        } else {
                            if (it.personaId != null) {
                                Color(0XFFCBD1D9)
                            } else {
                                MaterialTheme.colors.surface
                            }
                        }
                        Card(
                            elevation = 0.dp,
                            backgroundColor = backgroundColor,
                            onClick = {
                                selectedSocial = it
                            },
                            enabled = it.personaId == null,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = it.name,
                                    modifier = Modifier
                                        .weight(1f),
                                    color = contentColorFor(
                                        backgroundColor = backgroundColor
                                    )
                                )
                                if (it.personaId != null) {
                                    Text(
                                        text = "Connected",
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { selectedSocial?.let { onConnect.invoke(it) } },
                enabled = selectedSocial != null
            ) {
                if (socials.all { it.personaId != null }) {
                    Text(text = "Done")
                } else {
                    Text(text = "Connect")
                }
            }
        }
    }
}

@Composable
fun ConnectSocialModal(
    onDone: () -> Unit
) {
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Log in to continue", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Please log in to Twitter first and we will guide you through the next steps to connect to Mask Network.")
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onDone.invoke() },
            ) {
                Text(text = "I understand")
            }
        }
    }
}