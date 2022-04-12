/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.persona.ui.scenes

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.navigateUri
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskSelection
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.viewmodel.SwitchPersonaViewModel
import moe.tlaster.koin.compose.getViewModel
import moe.tlaster.precompose.navigation.NavController

@NavGraphDestination(
    route = PersonaRoute.SwitchPersona,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun SwitchPersonaModal(
    navController: NavController,
) {
    val viewModel = getViewModel<SwitchPersonaViewModel>()
    val currentPersonaData by viewModel.current.collectAsState(initial = null)
    val items by viewModel.items.collectAsState(initial = emptyList())

    MaskModal(
        title = {
            Text(
                text = stringResource(R.string.tab_personas),
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn {
                items(items) { item ->
                    MaskSelection(
                        selected = currentPersonaData == item,
                        enabled = currentPersonaData == null || currentPersonaData != item,
                        onClicked = {
                            viewModel.switch(item)
                        },
                        content = {
                            Image(
                                painter = painterResource(R.drawable.ic_persona_default_avatar),
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
                            navController.navigateUri(Uri.parse(Deeplinks.Persona.Register.CreatePersona))
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
