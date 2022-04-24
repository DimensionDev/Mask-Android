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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ext.navigateToHome
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.model.SocialProfile
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.viewmodel.social.UserNameModalViewModel
import moe.tlaster.precompose.navigation.NavController
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
    route = PersonaRoute.ConnectAccount.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet
)
@Composable
fun ConnectAccountModal(
    navController: NavController,
    @Path("personaId") personaId: String,
    @Path("profileId") profileId: String,
    @Path("avatar") avatar: String,
) {
    val socialProfile = remember(profileId) { SocialProfile.parse(profileId) } ?: return
    val viewModel = getViewModel<UserNameModalViewModel> {
        parametersOf(socialProfile)
    }
    val name by viewModel.userName.collectAsState()
    MaskModal(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberImagePainter(avatar),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                    )
                    Spacer(Modifier.width(18.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_connect_account_more),
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(18.dp))
                    Image(
                        painter = painterResource(R.drawable.mask),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(text = stringResource(R.string.scene_social_connect_to_mask_network))
            }
        }
    ) {
        Column {
            MaskInputField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { viewModel.setUserName(it) },
            )
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.done(personaId, name)
                    navController.navigateToHome()
                },
            ) {
                Text(text = stringResource(R.string.scene_social_connect_button_title))
            }
        }
    }
}
