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
package com.dimension.maskbook.wallet.walletconnect.v1.server

import android.content.Context
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.walletconnect.OnWalletConnectRequestListener
import com.dimension.maskbook.wallet.walletconnect.WCClientMeta
import com.dimension.maskbook.wallet.walletconnect.WalletConnectServerManager
import com.dimension.maskbook.wallet.walletconnect.v1.BaseWalletConnectManager
import com.dimension.maskbook.wallet.walletconnect.v1.WCSessionV1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.walletconnect.Session
import org.walletconnect.impls.FileWCSessionStore
import org.walletconnect.impls.WCSessionStore
import java.io.File

class WalletConnectServerManagerV1(private val context: Context) :
    BaseWalletConnectManager(),
    WalletConnectServerManager {

    private val _connectedSessions = MutableStateFlow(emptyList<WCSessionV1>())
    // TODO combine an updater with the connectedSessions flow
    // So it will be possible to update the connectedSessions flow without having to update the whole list
    override val connectedClients: Flow<List<WCClientMeta>>
        get() = _connectedSessions.map {
            it.mapNotNull { session ->
                if (session.approvedAccounts().isNullOrEmpty()) null else
                    session.peerMeta()?.toWcClientMeta(session.id)
            }
        }
    override val storage: WCSessionStore
        get() = FileWCSessionStore(
            File(context.cacheDir, "v1_session_store_server.json").apply {
                if (!this.exists()) createNewFile()
            },
            moshi
        )

    init {
        storage.list().forEach {
            if (it.approvedAccounts.isNullOrEmpty()) {
                // end unapproved sessions
                it.config.session().kill()
            } else {
                it.config.session().addToConnected()
            }
        }
    }

    private fun WCSessionV1.addToConnected() {
        addCallback(
            RequestCallback(
                session = this,
                onDisConnected = {
                    _connectedSessions.value = _connectedSessions.value.minus(it)
                },
                onRequest = { call, session -> handleRequest(call, session) }
            )
        )
        _connectedSessions.value = _connectedSessions.value.plus(this)
    }

    private fun handleRequest(call: Session.MethodCall, session: WCSessionV1) {
        when (call) {
            is Session.MethodCall.SendTransaction -> {
                // TODO dispatch transaction call to listener
            }
            is Session.MethodCall.SignMessage -> rejectUnSupported(session, call.id)
            is Session.MethodCall.Custom -> rejectUnSupported(session, call.id)
            else -> {}
        }
    }

    private fun rejectUnSupported(session: WCSessionV1, id: Long) {
        session.rejectRequest(
            id = id,
            errorCode = 404,
            errorMsg = "Unsupported method call",
        )
    }

    override fun connectClient(wcUri: String, onRequest: (clientMeta: WCClientMeta) -> Unit) {
        Session.Config
            .fromWCUri(wcUri)
            .toFullyQualifiedConfig()
            .session()
            .apply {
                addCallback(
                    ConnectionCallback(
                        session = this,
                        onRequest = onRequest,
                        onApprove = {
                            it.addToConnected()
                        }
                    )
                )
                init()
            }
    }

    override fun approveConnect(clientMeta: WCClientMeta, accounts: List<String>, chainId: Long) {
        getSessionById(clientMeta.id)?.approve(
            accounts = accounts,
            chainId = chainId
        )
    }

    override fun rejectConnect(clientMeta: WCClientMeta) {
        TODO("Not yet implemented")
    }

    override fun addOnRequestListener(listener: OnWalletConnectRequestListener) {
        TODO("Not yet implemented")
    }

    override fun removeOnRequestListener(listener: OnWalletConnectRequestListener) {
        TODO("Not yet implemented")
    }

    override fun approveRequest(clientMeta: WCClientMeta, requestId: String, response: Any) {
        TODO("Not yet implemented")
    }

    override fun rejectRequest(clientMeta: WCClientMeta, requestId: String, errorCode: Long, errorMessage: String) {
        TODO("Not yet implemented")
    }

    private fun getSessionById(id: String): WCSessionV1? {
        return _connectedSessions.value.firstOrNull { it.id == id }
    }

    // TODO TO BE REMOVE
    private class ConnectionCallback(
        private val session: WCSessionV1,
        private val onRequest: (clientMeta: WCClientMeta) -> Unit,
        private val onApprove: (session: WCSessionV1) -> Unit,
    ) : Session.Callback {
        override fun onMethodCall(call: Session.MethodCall) {
            if (call is Session.MethodCall.SessionRequest) {
                val clientMeta = session.peerMeta()?.toWcClientMeta(session.id)
                onRequest(clientMeta!!)
            }
        }

        override fun onStatus(status: Session.Status) {
            if (status == Session.Status.Approved) {
                onApprove(session)
                session.clearCallbacks()
            }
        }
    }

    // TODO unit connection request
    private class RequestCallback(
        private val session: WCSessionV1,
        private val onDisConnected: (session: WCSessionV1) -> Unit,
        private val onRequest: (call: Session.MethodCall, session: WCSessionV1) -> Unit
    ) : Session.Callback {
        override fun onMethodCall(call: Session.MethodCall) {
            onRequest(call, session)
        }

        override fun onStatus(status: Session.Status) {
            if (status is Session.Status.Error && BuildConfig.DEBUG) {
                status.throwable.printStackTrace()
            }
            if (status == Session.Status.Closed) {
                session.clearCallbacks()
                onDisConnected(session)
            }
        }
    }
}

private fun Session.PeerMeta.toWcClientMeta(id: String) = WCClientMeta(
    id = id,
    name = name ?: "",
    url = url ?: "",
    description = description ?: "",
    icons = icons ?: emptyList()
)
