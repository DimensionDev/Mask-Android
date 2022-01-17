package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.Network
import com.dimension.maskbook.wallet.repository.PersonaData
import com.dimension.maskbook.wallet.repository.SocialData
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.viewmodel.persona.PersonaViewModel
import org.koin.androidx.compose.getViewModel

@ExperimentalAnimationApi
@Composable
fun PersonaScene(
    onBack: () -> Unit,
    onPersonaNameClick: () -> Unit,
    onAddSocialClick: (PersonaData, Network?) -> Unit,
    onRemoveSocialClick: (PersonaData, SocialData) -> Unit,
) {
    val viewModel: PersonaViewModel = getViewModel()
    val currentPersona by viewModel.currentPersona.observeAsState(initial = null)
    val socialList by viewModel.socialList.collectAsState()
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    backgroundColor = if (socialList.isNullOrEmpty()) {
                        MaterialTheme.colors.background
                    } else {
                        MaterialTheme.colors.surface
                    },
                    actions = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                            )
                        }
                    },
                    title = {
                        currentPersona?.let { persona ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable(onClick = onPersonaNameClick)
                            ) {
                                Text(text = persona.name)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = null,
                                    modifier = Modifier.rotate(-90f)
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                socialList?.let { list ->
                    if (list.isNotEmpty()) {
                        PersonaInfoScene(
                            socialList = list,
                            onAddSocialClick = {
                                currentPersona?.let {
                                    onAddSocialClick(it, null)
                                }
                            },
                            onSocialItemClick = { data, isEditing ->
                                currentPersona?.let {
                                    if (isEditing) {
                                        onRemoveSocialClick(it, data)
                                    }
                                }
                            },
                        )
                    } else {
                        PersonaEmptyScene(
                            onItemClick = { network ->
                                currentPersona?.let {
                                    onAddSocialClick(it, network)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
