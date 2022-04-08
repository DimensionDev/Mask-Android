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
package com.dimension.maskbook.persona.ui.tab

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.dimension.maskbook.common.ext.navOptions
import com.dimension.maskbook.common.ext.navigateUri
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.PlatformType
import com.dimension.maskbook.persona.repository.IPersonaRepository
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.ui.scenes.PersonaScene
import com.dimension.maskbook.persona.ui.scenes.social.connectSocial
import moe.tlaster.koin.compose.get
import moe.tlaster.precompose.navigation.NavController

class PersonasTabScreen : TabScreen {
    override val route = CommonRoute.Main.Tabs.Persona
    override val title: Int = R.string.tab_personas
    override val icon: Int = R.drawable.ic_persona

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content(navController: NavController, onBack: () -> Unit) {
        val repository = get<IPersonaRepository>()
        PersonaScene(
            onBack = onBack,
            onPersonaCreateClick = {
                navController.navigateUri(Uri.parse(Deeplinks.Persona.Register.WelcomeCreatePersona))
            },
            onPersonaRecoveryClick = {
                navController.navigateUri(Uri.parse(Deeplinks.Persona.Recovery))
            },
            onPersonaNameClick = {
                navController.navigate(PersonaRoute.PersonaMenu)
            },
            onAddSocialClick = { persona, network ->
                network?.toPlatform()?.let {
                    connectSocial(
                        controller = navController,
                        personaId = persona.identifier,
                        platform = it,
                        repository = repository
                    )
                } ?: navController.navigate(PersonaRoute.SelectPlatform(persona.identifier))
            },
            onRemoveSocialClick = { persona, social ->
                social.network.toPlatform()?.let {
                    navController.navigate(
                        PersonaRoute.DisconnectSocial(
                            personaId = persona.identifier,
                            platform = it.name,
                            socialId = social.id,
                            personaName = persona.name,
                            socialName = social.name,
                        )
                    )
                }
            },
            onSocialItemClick = { _, social ->
                social.network.toPlatform()?.let {
                    repository.setPlatform(it)
                    navController.navigateUri(
                        Uri.parse(Deeplinks.WebContent(null)),
                        navOptions {
                            launchSingleTop = true
                            popUpTo(CommonRoute.Main.Home.path) {
                                inclusive = true
                            }
                        }
                    )
                }
            },
            onAddPersonaAvatar = {
                navController.navigate(PersonaRoute.SetAvatar)
            },
            onPersonaAvatarClick = {
                navController.navigate(PersonaRoute.PersonaAvatarModal)
            },
            onSynchronize = {
                navController.navigate(PersonaRoute.Synchronization.Scan)
            }
        )
    }
}

private fun Network.toPlatform() = when (this) {
    Network.Twitter -> PlatformType.Twitter
    Network.Facebook -> PlatformType.Facebook
    else -> null // TODO support other network
}
