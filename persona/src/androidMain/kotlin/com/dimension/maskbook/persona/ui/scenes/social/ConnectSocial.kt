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

import androidx.navigation.NavController
import com.dimension.maskbook.common.ext.navigateToExtension
import com.dimension.maskbook.common.ext.toSite
import com.dimension.maskbook.persona.export.model.PlatformType
import com.dimension.maskbook.persona.repository.IPersonaRepository
import com.dimension.maskbook.persona.route.PersonaRoute

fun connectSocial(
    controller: NavController,
    repository: IPersonaRepository,
    personaId: String,
    platform: PlatformType,
) {
    repository.beginConnectingProcess(
        personaId = personaId,
        platformType = platform,
    ) {
        controller.navigate(PersonaRoute.ConnectAccount(it.personaId, it.profile.id, it.profile.avatar))
    }
    controller.navigateToExtension(platform.toSite().name)
}
