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
import android.util.Log
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.walletconnect.WCClientMeta
import com.dimension.maskbook.wallet.walletconnect.WCRequest
import com.dimension.maskbook.wallet.walletconnect.WCRequestParams
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

    private var onRequest: (clientMeta: WCClientMeta, request: WCRequest) -> Unit = { _, _ -> }
    private val _pendingSessions = MutableStateFlow(emptyList<WCSessionV1>())
    private val _connectedSessions = MutableStateFlow(emptyList<WCSessionV1>())
    override val connectedClients: Flow<List<WCClientMeta>>
        get() = _connectedSessions.map {
            it.mapNotNull { session ->
                if (session.approvedAccounts().isNullOrEmpty()) null else
                    session.peerMeta()?.toWcClientMeta(session.id, accounts = session.approvedAccounts() ?: emptyList(), session.chainId ?: 0)
            }
        }
    override val storage: WCSessionStore
        get() = FileWCSessionStore(
            File(context.cacheDir, "v1_session_store_server.json").apply {
                if (!this.exists()) createNewFile()
            },
            moshi
        )

    override fun init(onRequest: (clientMeta: WCClientMeta, request: WCRequest) -> Unit) {
        this.onRequest = onRequest
        storage.list().forEach {
            if (it.approvedAccounts.isNullOrEmpty()) {
                // end unapproved sessions
                it.config.session().kill()
            } else {
                it.config.session().apply {
                    addToConnected()
                    init()
                }
            }
        }
    }

    override fun connectClient(wcUri: String, onRequest: (clientMeta: WCClientMeta) -> Unit) {
        Session.Config
            .fromWCUri(wcUri)
            .toFullyQualifiedConfig()
            .session()
            .apply {
                _pendingSessions.value += this
                addCallback(
                    ConnectionCallback(
                        session = this,
                        onRequest = onRequest,
                        onApprove = {
                            it.addToConnected()
                        },
                        onDisConnected = {
                            _pendingSessions.value -= it
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
        getSessionById(clientMeta.id)?.reject()
    }

    override fun approveRequest(clientMeta: WCClientMeta, requestId: String, response: Any) {
        getSessionById(clientMeta.id)?.approveRequest(id = requestId.toLong(), response = response)
    }

    override fun rejectRequest(clientMeta: WCClientMeta, requestId: String, errorCode: Long, errorMessage: String) {
        getSessionById(clientMeta.id)?.rejectRequest(id = requestId.toLong(), errorCode = errorCode, errorMsg = errorMessage)
    }

    private fun WCSessionV1.addToConnected() {
        addCallback(
            RequestCallback(
                session = this,
                onDisConnected = {
                    _connectedSessions.value -= it
                },
                onRequest = { call, session -> handleRequest(call, session) }
            )
        )
        _pendingSessions.value -= this
        _connectedSessions.value += this
    }

    private fun handleRequest(call: Session.MethodCall, session: WCSessionV1) {
        when (call) {
            is Session.MethodCall.SendTransaction -> {
                dispatchRequest(
                    WCRequest(
                        id = call.id.toString(),
                        params = WCRequestParams.WCTransaction(
                            from = call.from,
                            to = call.to,
                            value = call.value,
                            data = call.data,
                            gasLimit = call.gasLimit,
                            gasPrice = call.gasPrice,
                            nonce = call.nonce,
                        )
                    ),
                    session
                )
            }
            is Session.MethodCall.SignMessage -> dispatchRequest(
                WCRequest(
                    id = call.id.toString(),
                    params = WCRequestParams.WCSign(
                        address = call.address,
                        message = call.message
                    )
                ),
                session
            )
            is Session.MethodCall.Custom -> rejectUnSupported(session, call.id)
            else -> {}
        }
    }

    private fun dispatchRequest(wcRequest: WCRequest, session: WCSessionV1) {
        session.peerMeta()?.let {
            onRequest.invoke(it.toWcClientMeta(session.id, session.approvedAccounts() ?: emptyList(), session.chainId ?: 0), wcRequest)
        }
    }

    private fun rejectUnSupported(session: WCSessionV1, id: Long) {
        session.rejectRequest(
            id = id,
            errorCode = 404,
            errorMsg = "Unsupported method call",
        )
    }

    private fun getSessionById(id: String): WCSessionV1? {
        return _connectedSessions.value.firstOrNull { it.id == id }
            ?: _pendingSessions.value.firstOrNull { it.id == id }
    }

    private class ConnectionCallback(
        private val session: WCSessionV1,
        private val onRequest: (clientMeta: WCClientMeta) -> Unit,
        private val onApprove: (session: WCSessionV1) -> Unit,
        private val onDisConnected: (session: WCSessionV1) -> Unit
    ) : Session.Callback {
        override fun onMethodCall(call: Session.MethodCall) {
            "onMethodCall: $call".log()
            if (call is Session.MethodCall.SessionRequest) {
                val clientMeta = session.peerMeta()?.toWcClientMeta(session.id, session.approvedAccounts() ?: emptyList(), session.chainId ?: 0)
                onRequest(clientMeta!!)
            }
        }

        override fun onStatus(status: Session.Status) {
            "status: $status".log()
            when (status) {
                Session.Status.Approved -> {
                    session.clearCallbacks()
                    onApprove(session)
                }
                Session.Status.Closed -> {
                    session.clearCallbacks()
                    onDisConnected(session)
                }
                else -> {}
            }
        }
    }

    private class RequestCallback(
        private val session: WCSessionV1,
        private val onDisConnected: (session: WCSessionV1) -> Unit,
        private val onRequest: (call: Session.MethodCall, session: WCSessionV1) -> Unit,
    ) : Session.Callback {
        override fun onMethodCall(call: Session.MethodCall) {
            "onMethodCall: $call".log()
            onRequest(call, session)
        }

        override fun onStatus(status: Session.Status) {
            "status: $status".log()
            when (status) {
                Session.Status.Closed -> {
                    session.clearCallbacks()
                    onDisConnected(session)
                }
                is Session.Status.Error -> if (BuildConfig.DEBUG) status.throwable.printStackTrace()
                else -> {}
            }
        }
    }
}

private fun Session.PeerMeta.toWcClientMeta(id: String, accounts: List<String>, chainId: Long) = WCClientMeta(
    id = id,
    name = name ?: "",
    url = url ?: "",
    description = description ?: "",
    icons = icons ?: emptyList(),
    accounts = accounts,
    chainType = ChainType.values().find { it.chainId == chainId } ?: ChainType.eth,
)

private fun String.log() {
    if (BuildConfig.DEBUG) {
        Log.d("WalletConnectServer", this)
    }
}
