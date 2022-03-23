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
package com.dimension.maskbook.persona.route

import com.dimension.maskbook.common.routeProcessor.annotations.Route

@Suppress("CONST_VAL_WITHOUT_INITIALIZER")
@Route
expect object PersonaRoute {
    const val Logout: String
    const val PersonaMenu: String
    const val SwitchPersona: String
    const val SetAvatar: String
    const val PersonaAvatarModal: String
    object RenamePersona {
        operator fun invoke(personaId: String): String
    }
    const val ExportPrivateKey: String
    object SelectPlatform {
        operator fun invoke(personaId: String): String
    }
    object ConnectAccount {
        operator fun invoke(personaId: String, profile: String): String
    }
    object DisconnectSocial {
        operator fun invoke(
            personaId: String,
            platform: String,
            socialId: String,
            personaName: String?,
            socialName: String?
        ): String
    }
}
