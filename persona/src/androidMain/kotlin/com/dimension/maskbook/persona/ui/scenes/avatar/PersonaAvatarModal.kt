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
package com.dimension.maskbook.persona.ui.scenes.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.button.MaskListButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.repository.IPersonaRepository
import com.dimension.maskbook.persona.route.PersonaRoute
import org.koin.androidx.compose.get

@NavGraphDestination(
    route = PersonaRoute.PersonaAvatarModal,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun PersonaAvatarModal(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val repository = get<IPersonaRepository>()
    MaskModal {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MaskListButton(
                onClick = {
                    navController.navigate(PersonaRoute.SetAvatar)
                },
                text = {
                    Text(text = "Set Persona Avatar")
                },
                icon = R.drawable.image
            )
            MaskListButton(
                onClick = {
                    repository.setAvatarForCurrentPersona(null)
                    onBack.invoke()
                },
                text = {
                    Text(text = "Remove Persona Avatar", color = Color.Red)
                },
                icon = R.drawable.ic_delete_wallet,
            )
        }
    }
}
