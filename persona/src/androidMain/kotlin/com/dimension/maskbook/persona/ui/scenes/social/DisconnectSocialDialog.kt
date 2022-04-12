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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.routeProcessor.annotations.Query
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.export.model.PlatformType
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.viewmodel.social.DisconnectSocialViewModel
import org.koin.androidx.compose.getViewModel

@NavGraphDestination(
    route = PersonaRoute.DisconnectSocial.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun DisconnectSocialDialog(
    navController: NavController,
    @Back onBack: () -> Unit,
    @Path("platform") platform: String,
    @Path("personaId") personaId: String,
    @Path("socialId") socialId: String,
    @Query("personaName") personaName: String?,
    @Query("socialName") socialName: String?,
) {
    val viewModel = getViewModel<DisconnectSocialViewModel>()

    MaskDialog(
        onDismissRequest = { onBack.invoke() },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_note),
                contentDescription = null
            )
        },
        text = {
            Text(
                text = stringResource(
                    R.string.common_alert_disconnect_profile_title,
                    socialName.orEmpty(),
                    personaName.orEmpty(),
                )
            )
        },
        buttons = {
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onBack.invoke()
                    }
                ) {
                    Text(text = stringResource(R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        when (PlatformType.valueOf(platform)) {
                            PlatformType.Twitter ->
                                viewModel.disconnectProfile(
                                    personaId = personaId,
                                    socialId = socialId
                                )
                            PlatformType.Facebook ->
                                viewModel.disconnectProfile(
                                    personaId = personaId,
                                    socialId = socialId
                                )
                        }
                        navController.popBackStack()
                    },
                ) {
                    Text(text = stringResource(R.string.common_controls_confirm))
                }
            }
        }
    )
}
