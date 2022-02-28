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
package com.dimension.maskbook.extension

import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.export.model.Site
import com.dimension.maskbook.extension.repository.ExtensionRepository
import kotlinx.coroutines.flow.Flow

class ExtensionServicesImpl(
    private val repository: ExtensionRepository,
) : ExtensionServices {
    override val site: Flow<Site>
        get() = repository.currentSite

    override fun setSite(site: Site) {
        repository.setCurrentSite(site)
    }

    override val isExtensionActive: Flow<Boolean>
        get() = repository.isExtensionConnected
}
