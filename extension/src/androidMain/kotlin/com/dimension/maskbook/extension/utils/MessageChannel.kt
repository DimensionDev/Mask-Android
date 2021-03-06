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
package com.dimension.maskbook.extension.utils

import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.extension.export.model.ExtensionId
import com.dimension.maskbook.extension.export.model.ExtensionMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal abstract class MessageChannel(
    private val flow: Flow<JSONObject>,
    private val scope: CoroutineScope,
) {
    private val queue = ConcurrentHashMap<String, Channel<String?>>()

    private val _extensionMessage = MutableSharedFlow<ExtensionMessage>(extraBufferCapacity = 50)
    val extensionMessage: Flow<ExtensionMessage> = _extensionMessage.asSharedFlow()

    protected abstract fun sendMessage(message: JSONObject)

    fun startMessageCollect() {
        flow.onEach { onMessage(it) }
            .launchIn(scope)
    }

    fun sendResponseMessage(map: Map<String, Any?>) {
        sendMessage(JSONObject(map))
    }

    suspend fun executeMessage(
        method: String,
        isWait: Boolean = true,
        params: Map<String, Any> = emptyMap(),
    ): String? {
        val id = UUID.randomUUID().toString()
        val message = JSONObject(
            mapOf(
                "id" to id,
                "jsonrpc" to "2.0",
                "method" to method,
                "params" to JSONObject(params)
            )
        )

        // some method will not return, don't receive
        if (!isWait) {
            sendMessage(message)
            return null
        }

        val channel = Channel<String?>()
        try {
            queue[id] = channel
            sendMessage(message)
            return channel.receive()
        } finally {
            channel.close()
        }
    }

    fun subscribeMessage(vararg method: String): Flow<ExtensionMessage> {
        return _extensionMessage.filter { it.method in method }
    }

    private fun onMessage(jsonObject: JSONObject) {
        val messageId = runCatching {
            jsonObject.get("id")
        }.getOrNull()
        val result = runCatching {
            jsonObject.get("result")
        }.getOrNull()?.toString()?.takeIf {
            it != "null"
        }
        if (messageId != null && queue.containsKey(messageId)) {
            queue.remove(messageId)?.trySend(result)
        } else {
            val method = runCatching {
                jsonObject.getString("method")
            }.getOrNull()
            val params = runCatching {
                jsonObject.get("params")
            }.getOrNull()?.toString()?.takeIf {
                it != "null"
            }
            val jsonrpc = kotlin.runCatching {
                jsonObject.getString("jsonrpc")
            }.getOrDefault("2.0")
            if (method != null) {
                _extensionMessage.tryEmit(
                    ExtensionMessage(
                        id = ExtensionId.fromAny(messageId),
                        jsonrpc = jsonrpc,
                        method = method,
                        params = params,
                        onResponse = { sendResponseMessage(it) },
                    )
                )
            }
        }
    }
}

internal class BackgroundMessageChannel(
    private val controller: WebContentController,
    scope: CoroutineScope,
) : MessageChannel(
    flow = controller.backgroundMessage,
    scope = scope,
) {
    override fun sendMessage(message: JSONObject) {
        controller.sendBackgroundMessage(message)
    }
}

internal class ContentMessageChannel(
    private val controller: WebContentController,
    scope: CoroutineScope,
) : MessageChannel(
    flow = controller.contentMessage,
    scope = scope,
) {
    override fun sendMessage(message: JSONObject) {
        controller.sendContentMessage(message)
    }
}
