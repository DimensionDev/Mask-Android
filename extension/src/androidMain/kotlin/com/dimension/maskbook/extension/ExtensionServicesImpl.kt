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
import com.dimension.maskbook.extension.export.model.ExtensionResponseMessage
import com.dimension.maskbook.extension.export.model.Site
import com.dimension.maskbook.extension.repository.ExtensionRepository
import com.dimension.maskbook.extension.utils.MessageChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import org.koin.core.annotation.Single

@Single(binds = [ExtensionServices::class])
internal class ExtensionServicesImpl(
    private val repository: ExtensionRepository,
    private val messageChannel: MessageChannel,
) : ExtensionServices {
    override val site: Flow<Site>
        get() = repository.currentSite

    override fun setSite(site: Site) {
        repository.setCurrentSite(site)
    }

    override val isExtensionActive: Flow<Boolean>
        get() = repository.isExtensionConnected

    override suspend fun ensureExtensionActive() {
        isExtensionActive.first { it }
    }

    override suspend fun runJSMethod(method: String, vararg args: Pair<String, Any>): String {
        return messageChannel.execute<String>(method, args.toMap()).orEmpty()
    }

    suspend inline fun <reified T : Any> execute(method: String, vararg args: Pair<String, Any>): T? {
        return messageChannel.execute(method, args.toMap())
    }

    override fun subscribeJSEvent(method: String): Flow<ExtensionMessage> {
        return messageChannel.subscribeMessage(method).mapNotNull { it }
    }

    override fun sendJSEventResponse(response: ExtensionResponseMessage) {
        messageChannel.sendResponseMessage(response)
    }
}
