package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
    currentPersonaData: PersonaData,
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
            Text(text = stringResource(R.string.tab_personas), style = MaterialTheme.typography.h6)
            LazyColumn {
                items(items) {
                    MaskSelection(
                        selected = currentPersonaData == it,
                        enabled = currentPersonaData != it,
                        onClicked = {
                            onItemClicked.invoke(it)
                        },
                        content = {
                            Icon(Icons.Default.AccountCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .weight(1f),
                                style = MaterialTheme.typography.subtitle1
                            )
                        }
                    )
                }
                item {
                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        Card(
                            elevation = 0.dp,
                            backgroundColor = MaterialTheme.colors.background,
                            onClick = {
                                onAdd.invoke()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.scene_personas_add_persona),
                                    modifier = Modifier
                                        .weight(1f),
                                    style = MaterialTheme.typography.subtitle1
                                )
                                Image(
                                    painterResource(id = R.drawable.ic_plus),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
