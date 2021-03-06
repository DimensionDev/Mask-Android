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
import com.dimension.maskbook.extension.export.model.ExtensionMessage
import com.dimension.maskbook.extension.export.model.Site
import com.dimension.maskbook.extension.repository.ExtensionRepository
import com.dimension.maskbook.extension.utils.BackgroundMessageChannel
import com.dimension.maskbook.extension.utils.ContentMessageChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

internal class ExtensionServicesImpl(
    private val repository: ExtensionRepository,
    private val backgroundMessageChannel: BackgroundMessageChannel,
    private val contentMessageChannel: ContentMessageChannel,
) : ExtensionServices {
    override val site: Flow<Site>
        get() = repository.currentSite

    override fun setSite(site: Site) {
        repository.setCurrentSite(site)
    }

    override val isExtensionActive: Flow<Boolean>
        get() = repository.isExtensionConnected

    override fun loadUrl(url: String) {
        repository.loadUrl(url)
    }

    override fun refresh() {
        repository.refresh()
    }

    override suspend fun ensureExtensionActive() {
        isExtensionActive.first { it }
    }

    override suspend fun runBackgroundJSMethod(method: String, isWait: Boolean, vararg args: Pair<String, Any>): String? {
        return backgroundMessageChannel.executeMessage(method, isWait, args.toMap())
    }

    override fun subscribeBackgroundJSEvent(vararg method: String): Flow<ExtensionMessage> {
        return backgroundMessageChannel.subscribeMessage(*method)
    }

    override fun sendBackgroundJSEventResponse(map: Map<String, Any?>) {
        backgroundMessageChannel.sendResponseMessage(map)
    }

    override suspend fun runCurrentContentJSMethod(
        method: String,
        isWait: Boolean,
        vararg args: Pair<String, Any>
    ): String? {
        return contentMessageChannel.executeMessage(method, isWait, args.toMap())
    }

    override fun sendCurrentContentJSEventResponse(map: Map<String, Any?>) {
        contentMessageChannel.sendResponseMessage(map)
    }

    override fun subscribeCurrentContentJSEvent(vararg method: String): Flow<ExtensionMessage> {
        return contentMessageChannel.subscribeMessage(*method)
    }
}
