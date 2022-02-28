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
package com.dimension.maskbook.persona.ui.scenes.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.button.MaskGridButton
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.model.icon
import com.dimension.maskbook.persona.model.platform
import com.dimension.maskbook.persona.model.title
import com.dimension.maskbook.persona.route.PersonaRoute

private val items = listOf(
    Network.Twitter,
    Network.Facebook,
)

@NavGraphDestination(
    route = PersonaRoute.SelectPlatform.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun SelectPlatformModal(
    navController: NavController,
    @Path("personaId") personaId: String,
) {
    MaskModal(
        title = {
            Text(
                text = "Connect Social accounts",
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                items.forEach { item ->
                    MaskGridButton(
                        modifier = Modifier.size(SelectPlatformModalDefaults.itemSize),
                        onClick = {
                            item.platform?.let {
                                navController.navigate(
                                    PersonaRoute.ConnectSocial(personaId, it.name)
                                )
                            }
                        },
                        icon = {
                            Image(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                                modifier = Modifier.size(SelectPlatformModalDefaults.iconSize)
                            )
                        },
                        text = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.subtitle2,
                            )
                        }
                    )
                }
                Spacer(Modifier.width(SelectPlatformModalDefaults.itemSize))
            }
        }
    }
}

private object SelectPlatformModalDefaults {
    val itemSize = 100.dp
    val iconSize = 48.dp
}
