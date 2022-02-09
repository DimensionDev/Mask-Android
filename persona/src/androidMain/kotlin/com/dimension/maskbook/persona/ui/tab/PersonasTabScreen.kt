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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.dimension.maskbook.common.ui.LocalRootNavController
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.repository.Network
import com.dimension.maskbook.wallet.repository.PlatformType
import com.dimension.maskbook.wallet.ui.scenes.persona.PersonaScene

class PersonasTabScreen : TabScreen {
    override val route = "Personas"
    override val title: Int = R.string.tab_personas
    override val icon: Int = R.drawable.ic_persona

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content(onBack: () -> Unit) {
        val rootNavController = LocalRootNavController.current
        PersonaScene(
            onBack = onBack,
            onPersonaCreateClick = {
                rootNavController.navigate("WelcomeCreatePersona")
            },
            onPersonaRecoveryClick = {
                rootNavController.navigate("Recovery")
            },
            onPersonaNameClick = {
                rootNavController.navigate("PersonaMenu")
            },
            onAddSocialClick = { persona, network ->
                val platform = when (network) {
                    Network.Twitter -> PlatformType.Twitter
                    Network.Facebook -> PlatformType.Facebook
                    else -> null // TODO support other network
                }
                if (platform == null) {
                    rootNavController.navigate("SelectPlatform/${persona.id.encodeUrl()}")
                } else {
                    rootNavController.navigate("ConnectSocial/${persona.id.encodeUrl()}/$platform")
                }
            },
            onRemoveSocialClick = { persona, social ->
                val platform = when (social.network) {
                    Network.Twitter -> PlatformType.Twitter
                    Network.Facebook -> PlatformType.Facebook
                    else -> null // TODO support other network
                }
                if (platform != null) {
                    rootNavController.navigate(
                        "DisconnectSocial/${persona.id.encodeUrl()}/$platform/${social.id.encodeUrl()}" +
                            "?personaName=${persona.name.encodeUrl()}&socialName=${social.name.encodeUrl()}"
                    )
                }
            },
        )
    }
}
