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

import com.dimension.maskbook.repository.Web3Request
import org.json.JSONObject

data class ExtensionResponseMessage(
    override val id: Any,
    override val jsonrpc: String,
    val result: ExtensionResponse
) : ExtensionResponse {
    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "jsonrpc" to jsonrpc,
            "result" to JSONObject(result.toMap()).toString(),
        )
    }
    companion object {
        inline fun <reified T> success(request: Web3Request, result: T?): ExtensionResponseMessage {
            requireNotNull(request.payload)
            return ExtensionResponseMessage(
                id = request.id,
                jsonrpc = request.payload.jsonrpc,
                result = SuccessResult(
                    id = request.payload.id,
                    jsonrpc = request.payload.jsonrpc,
                    result = result
                )
            )
        }

        fun error(request: Web3Request, error: String): ExtensionResponseMessage {
            requireNotNull(request.payload)
            return ExtensionResponseMessage(
                id = request.id,
                jsonrpc = request.payload.jsonrpc,
                result = ErrorResult(
                    id = request.payload.id,
                    jsonrpc = request.payload.jsonrpc,
                    error = error,
                )
            )
        }
    }
}

sealed interface ExtensionResponse {
    val id: Any
    val jsonrpc: String
    fun toMap(): Map<String, Any?>
}

data class SuccessResult<T>(
    override val id: Any,
    override val jsonrpc: String,
    val result: T?
) : ExtensionResponse {
    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "result" to result,
            "id" to id,
            "jsonrpc" to jsonrpc,
        )
    }
}

data class ErrorResult(
    override val id: Any,
    override val jsonrpc: String,
    val error: String,
) : ExtensionResponse {
    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "error" to error,
            "id" to id,
            "jsonrpc" to jsonrpc,
        )
    }
}
