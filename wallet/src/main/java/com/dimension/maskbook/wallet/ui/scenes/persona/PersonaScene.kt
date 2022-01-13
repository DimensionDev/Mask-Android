package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.observeAsState
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
) {
    val viewModel: PersonaViewModel = getViewModel()
    val persona by viewModel.persona.observeAsState(initial = null)
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    backgroundColor = MaterialTheme.colors.surface,
                    actions = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                            )
                        }
                    },
                    title = {
                        persona?.let { persona ->
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
            Crossfade(
                targetState = true,
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            ) { hasAccount ->
                if (hasAccount) {
                    PersonaInfoScene()
                } else {
                    PersonaEmptyScene(
                        onItemClick = { item ->

                        }
                    )
                }
            }
        }
    }
}
