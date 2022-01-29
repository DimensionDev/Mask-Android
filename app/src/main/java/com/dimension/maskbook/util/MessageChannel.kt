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
package com.dimension.maskbook.util

import com.dimension.maskbook.component.WebExtensionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock

object MessageChannel {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val queue = linkedMapOf<String, Channel<String?>>()
    private val subscription = arrayListOf<Pair<String, MutableStateFlow<ExtensionMessage?>>>()
    val lock = ReentrantLock()
    var manager: WebExtensionManager? = null

    fun sendResponseMessage(message: ExtensionResponseMessage) {
        manager?.sendRawMessage(JSONObject(message.toMap()))
    }

    suspend inline fun <reified T : Any> execute(
        method: String,
        params: Map<String, String> = emptyMap()
    ): T? {
        val result = executeMessage(method, params)
        return when (T::class) {
            Short::class -> result?.toShortOrNull() as T?
            Int::class -> result?.toIntOrNull() as T?
            Long::class -> result?.toLongOrNull() as T?
            Float::class -> result?.toFloatOrNull() as T?
            Double::class -> result?.toDoubleOrNull() as T?
            Boolean::class -> result?.toBooleanStrictOrNull() as T?
            String::class -> result as T?
            else -> null
        }
    }

    suspend fun executeMessage(
        method: String,
        params: Map<String, Any> = emptyMap(),
    ): String? {
        return manager?.let {
            val id = UUID.randomUUID().toString()
            val channel = Channel<String?>()
            try {
                queue[id] = channel
                it.sendMessage(id, method, JSONObject(params))
                return channel.receive()
            } finally {
                channel.close()
            }
        }
    }

    suspend fun executeMessage(
        method: String,
        vararg params: String,
    ): String? {
        return manager?.let {
            val id = UUID.randomUUID().toString()
            val channel = Channel<String?>()
            try {
                queue[id] = channel
                it.sendMessage(id, method, params.toList())
                return channel.receive()
            } finally {
                channel.close()
            }
        }
    }

    fun subscribeMessage(method: String): Flow<ExtensionMessage?> {
        val flow = MutableStateFlow<ExtensionMessage?>(null)
        subscription.add(method to flow)
        return flow
    }

    fun onMessage(jsonObject: JSONObject) {
        scope.launch {
            val messageId = runCatching {
                jsonObject.get("id")
            }.getOrNull()
            val result = runCatching {
                jsonObject.get("result")
            }.getOrNull()?.toString()?.takeIf {
                it != "null"
            }
            if (messageId != null && queue.containsKey(messageId)) {
                queue[messageId]?.send(result)
                queue.remove(messageId)
            } else if (messageId != null) {
                val method = runCatching {
                    jsonObject.getString("method")
                }.getOrNull()
                val params = runCatching {
                    jsonObject.get("params")
                }.getOrNull()?.toString()?.takeIf {
                    it != "null"
                }
                if (method != null && subscription.any { it.first == method }) {
                    subscription.filter { it.first == method }.forEach {
                        it.second.value = ExtensionMessage(messageId, params)
                    }
                }
            }
        }
    }
}

data class ExtensionMessage(
    val id: Any,
    val params: String?,
)
