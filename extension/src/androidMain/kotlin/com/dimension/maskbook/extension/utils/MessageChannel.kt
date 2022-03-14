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

import android.util.Log
import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.extension.export.model.ExtensionMessage
import com.dimension.maskbook.extension.export.model.ExtensionResponseMessage
import com.dimension.maskbook.extension.ext.toMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal class MessageChannel(
    private val controller: WebContentController,
    private val scope: CoroutineScope,
) {
    private val queue = ConcurrentHashMap<String, Channel<String?>>()
    private val subscription = arrayListOf<Pair<String, MutableStateFlow<ExtensionMessage?>>>()

    private val _extensionMessage = Channel<ExtensionMessage>()
    val extensionMessage: Flow<ExtensionMessage> = _extensionMessage.consumeAsFlow()

    fun startMessageCollect() {
        controller.message
            .filterNotNull()
            .onEach { onMessage(it) }
            .launchIn(scope)
    }

    fun sendResponseMessage(message: ExtensionResponseMessage) {
        controller.sendMessage(JSONObject(message.toMap()))
    }

    suspend fun executeMessage(
        method: String,
        params: Map<String, Any> = emptyMap(),
    ): String? {
        val id = UUID.randomUUID().toString()
        val channel = Channel<String?>()
        try {
            queue[id] = channel
            controller.sendMessage(
                JSONObject(
                    mapOf(
                        "id" to id,
                        "jsonrpc" to "2.0",
                        "method" to method,
                        "params" to JSONObject(params)
                    )
                )
            )
            return channel.receive()
        } finally {
            channel.close()
        }
    }

    fun subscribeMessage(method: String): Flow<ExtensionMessage?> {
        val flow = MutableStateFlow<ExtensionMessage?>(null)
        subscription.add(method to flow)
        return flow
    }

    private suspend fun onMessage(jsonObject: JSONObject) {
        val messageId = runCatching {
            jsonObject.get("id")
        }.getOrNull()
        val result = runCatching {
            jsonObject.get("result")
        }.getOrNull()?.toString()?.takeIf {
            it != "null"
        }
        if (messageId != null && queue.containsKey(messageId)) {
            queue.remove(messageId)?.send(result)
        } else if (messageId != null) {
            val method = runCatching {
                jsonObject.getString("method")
            }.getOrNull()
            val params = runCatching {
                jsonObject.get("params")
            }.getOrNull()?.toString()?.takeIf {
                it != "null"
            }
            if (method != null) {
                if (subscription.any { it.first == method }) {
                    subscription.filter { it.first == method }.forEach { pair ->
                        pair.second.value = ExtensionMessage(
                            id = messageId,
                            method = method,
                            params = params,
                        ) {
                            sendResponseMessage(it)
                        }
                    }
                }
                _extensionMessage.trySend(
                    ExtensionMessage(
                        id = messageId,
                        method = method,
                        params = params,
                    ) {
                        sendResponseMessage(it)
                    }
                ).onFailure {
                    Log.w("MessageChannel", it)
                }
            }
        }
    }
}
