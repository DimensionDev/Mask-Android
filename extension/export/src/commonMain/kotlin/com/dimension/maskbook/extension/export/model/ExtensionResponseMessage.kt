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
package com.dimension.maskbook.extension.export.model

data class ExtensionResponseMessage(
    val id: Any,
    val jsonrpc: String,
    val result: ExtensionResponse
) {
    companion object {
        fun <T> success(
            messageId: Any,
            jsonrpc: String,
            payloadId: Any,
            result: T?
        ): ExtensionResponseMessage {
            return ExtensionResponseMessage(
                id = messageId,
                jsonrpc = jsonrpc,
                result = SuccessResult(
                    id = payloadId,
                    jsonrpc = jsonrpc,
                    result = result
                )
            )
        }

        fun error(
            messageId: Any,
            jsonrpc: String,
            payloadId: Any,
            error: String
        ): ExtensionResponseMessage {
            return ExtensionResponseMessage(
                id = messageId,
                jsonrpc = jsonrpc,
                result = ErrorResult(
                    id = payloadId,
                    jsonrpc = jsonrpc,
                    error = error,
                )
            )
        }
    }
}
