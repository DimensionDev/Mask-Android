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
package com.dimension.maskbook.labs.data

import com.dimension.maskbook.common.route.Navigator
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.labs.route.LabsRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RedPacketMethod(
    private val scope: CoroutineScope,
    private val services: ExtensionServices,
) {

    fun startSubscribe() {
        services.subscribeJSEvent(notifyRedPacket, claimOrRefundRedPacket)
            .onEach { message ->
                when (message.method) {
                    notifyRedPacket -> {
                        // val params = message.params ?: return@onEach
                        // Navigator.navigate(LabsRoute.RedPacket.LuckyDrop(params))
                    }
                    claimOrRefundRedPacket -> {
                        val params = message.params ?: return@onEach
                        Navigator.navigate(LabsRoute.RedPacket.LuckyDrop(params))
                    }
                }
            }
            .launchIn(scope)
    }

    companion object {
        private const val notifyRedPacket = "notifyRedpacket"
        private const val claimOrRefundRedPacket = "claimOrRefundRedpacket"
    }
}
