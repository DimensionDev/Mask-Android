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
package com.dimension.maskbook.wallet.walletconnect.v1

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.walletconnect.Session
import org.walletconnect.impls.MoshiPayloadAdapter
import org.walletconnect.impls.OkHttpTransport
import org.walletconnect.impls.WCSessionStore

abstract class BaseWalletConnectManager {
    protected val moshi: Moshi by lazy {
        Moshi.Builder().build()
    }
    protected val client: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    protected abstract val storage: WCSessionStore

    protected fun Session.FullyQualifiedConfig.session() = WCSessionV1(
        config = this,
        payloadAdapter = MoshiPayloadAdapter(moshi),
        sessionStore = storage,
        transportBuilder = OkHttpTransport.Builder(client = client, moshi = moshi),
        clientMeta = Session.PeerMeta(
            name = "Mask Network",
            url = "https://mask.io",
            description = "Mask Network"
        )
    )
}
