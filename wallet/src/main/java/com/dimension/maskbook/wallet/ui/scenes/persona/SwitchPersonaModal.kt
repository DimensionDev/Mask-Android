package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.PersonaData
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskSelection
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwitchPersonaModal(
    currentPersonaData: PersonaData?,
    items: List<PersonaData>,
    onAdd: () -> Unit,
    onItemClicked: (PersonaData) -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.tab_personas),
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn {
                items(items) { item ->
                    MaskSelection(
                        selected = currentPersonaData == item,
                        enabled = currentPersonaData == null || currentPersonaData != item,
                        onClicked = {
                            onItemClicked.invoke(item)
                        },
                        content = {
                            Image(
                                painter = painterResource(R.drawable.ic_default_persona_avatar),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = item.name)
                        }
                    )
                }
                item {
                    MaskSelection(
                        selected = false,
                        onClicked = {
                            onAdd.invoke()
                        },
                        content = {
                            Text(
                                text = stringResource(R.string.scene_personas_add_persona),
                            )
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    )
                }
            }
        }
    }
}
