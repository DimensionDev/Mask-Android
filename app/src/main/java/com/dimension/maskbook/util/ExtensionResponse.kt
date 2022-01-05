package com.dimension.maskbook.util

import com.dimension.maskbook.repository.Web3Request
import org.json.JSONObject

data class ExtensionResponseMessage(
    override val id: Any,
    override val jsonrpc: String,
    val result: ExtensionResponse
): ExtensionResponse {
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
): ExtensionResponse {
    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "error" to error,
            "id" to id,
            "jsonrpc" to jsonrpc,
        )
    }
}