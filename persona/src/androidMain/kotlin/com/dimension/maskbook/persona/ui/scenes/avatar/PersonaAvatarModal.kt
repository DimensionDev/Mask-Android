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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.button.MaskListItemButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.repository.IPersonaRepository
import com.dimension.maskbook.persona.route.PersonaRoute
import moe.tlaster.koin.compose.get
import moe.tlaster.precompose.navigation.NavController

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
            MaskListItemButton(
                onClick = {
                    navController.navigate(PersonaRoute.SetAvatar)
                },
                text = {
                    Text(text = stringResource(R.string.scene_persona_avatar_set_persona_avatar))
                },
                icon = R.drawable.image
            )
            MaskListItemButton(
                onClick = {
                    repository.setAvatarForCurrentPersona(null)
                    onBack.invoke()
                },
                text = {
                    Text(text = stringResource(R.string.scene_persona_avatar_remove_persona_avatar), color = Color.Red)
                },
                icon = R.drawable.ic_delete_wallet,
            )
        }
    }
}
