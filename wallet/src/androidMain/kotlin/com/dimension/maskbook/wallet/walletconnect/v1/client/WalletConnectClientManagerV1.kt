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
package com.dimension.maskbook.wallet.walletconnect.v1.client

import android.content.Context
import android.util.Log
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.ext.ether
import com.dimension.maskbook.wallet.walletconnect.WCError
import com.dimension.maskbook.wallet.walletconnect.WCResponder
import com.dimension.maskbook.wallet.walletconnect.WalletConnectClientManager
import com.dimension.maskbook.wallet.walletconnect.v1.BaseWalletConnectManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.komputing.khex.extensions.toNoPrefixHexString
import org.walletconnect.Session
import org.walletconnect.impls.FileWCSessionStore
import java.io.File
import java.util.Random
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class WalletConnectClientManagerV1(private val context: Context) : BaseWalletConnectManager(), WalletConnectClientManager {
    private var config: Session.Config? = null
    private var session: Session? = null
    private val bridgeServer = "https://safe-walletconnect.gnosis.io"
    private var onDisconnect: (address: String) -> Unit = {}
    private val connectedSessions = ConcurrentHashMap<String, Session>()
    private val onResponseStore = ConcurrentHashMap<Long, (response: Any, error: Throwable?) -> Unit>()

    override val storage by lazy {
        FileWCSessionStore(
            File(context.cacheDir, "v1_session_store.json").apply {
                if (!this.exists()) createNewFile()
            },
            moshi
        )
    }

    private val _wcUrl = MutableStateFlow("")
    override val wcUrl: Flow<String>
        get() = _wcUrl

    override fun connect(onResult: (success: Boolean, responder: WCResponder?) -> Unit) {
        session?.clearCallbacks()
        if (session == null) {
            Session.Config(
                handshakeTopic = UUID.randomUUID().toString(),
                bridge = bridgeServer,
                key = ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
            ).let {
                config = it
                session = it.toFullyQualifiedConfig().session().apply {
                    offer()
                }
            }
        }
        session?.apply {
            addCallback(
                PairCallback(
                    session = this,
                    onResult = { success, responder ->
                        onResult.invoke(
                            success,
                            responder
                        )
                        if (success) {
                            session?.let {
                                addToConnected(it, config?.handshakeTopic ?: "")
                            }
                        } else {
                            session?.kill()
                        }
                        resetSession()
                    },
                    onConnect = {
                        _wcUrl.value = config?.toWCUri() ?: ""
                    }
                )
            )
        }
    }

    private fun resetSession() {
        session = null
        config = null
        _wcUrl.value = ""
    }

    override fun disConnect(address: String): Boolean {
        return connectedSessions[address]?.let {
            it.kill()
            it.clearCallbacks()
            true
        } ?: false
    }

    override fun initSessions(onDisconnect: (address: String) -> Unit) {
        this.onDisconnect = onDisconnect
        storage.list().forEach {
            if (it.handshakeId == null && it.peerData == null) {
                // end unpaired sessions
                it.config.session().kill()
            } else {
                addToConnected(it.config.session(), it.config.handshakeTopic)
            }
        }
    }

    override fun sendToken(
        amount: BigDecimal,
        fromAddress: String,
        toAddress: String,
        data: String,
        gasLimit: Double,
        gasPrice: BigDecimal,
        onResponse: (response: Any, error: Throwable?) -> Unit
    ) {
        connectedSessions[fromAddress]?.let {
            val id = System.currentTimeMillis()
            onResponseStore[id] = onResponse
            it.performMethodCall(
                Session.MethodCall.SendTransaction(
                    id = id,
                    from = fromAddress,
                    to = toAddress,
                    nonce = "",
                    // calculation of gas fee in wallet connect sdk was wrong
                    gasPrice = null, // "0x${gasPrice.ether.wei.toBigInteger().toString(16)}",
                    gasLimit = null, // "0x${gasLimit.toBigDecimal().toBigInteger().toString(16)}",
                    data = data,
                    value = "0x${amount.ether.wei.toLong().toBigInteger().toString(16)}"
                ),
                callback = {
                    "Transaction response:$it".log()
                }
            )
        }
    }

    private fun addToConnected(session: Session, handshakeTopic: String) {
        session.approvedAccounts()?.forEach {
            connectedSessions[it] = session.apply {
                addCallback(
                    ConnectedSessionCallback(this, it, onDisconnect = { address, _ ->
                        // remove from connected sessions
                        connectedSessions.remove(address)
                        this@WalletConnectClientManagerV1.onDisconnect.invoke(address)
                    }, onResponse = { id, response, error ->
                        onResponseStore.remove(id)?.invoke(
                            response,
                            error
                        )
                    })
                )
                update(
                    approvedAccounts() ?: emptyList(),
                    chainId = storage.load(handshakeTopic)?.chainId ?: -1
                )
            }
        }
    }
}

private fun Session.responder(chainId: Long?) = peerMeta()?.let {
    chainId?.let { id ->
        ChainType.values().find { type ->
            type.chainId == id
        }
    }?.let { chainType ->
        WCResponder(
            accounts = approvedAccounts() ?: emptyList(),
            name = it.name ?: "",
            description = it.description ?: "",
            icons = it.icons ?: emptyList(),
            url = it.url ?: "",
            chainType = chainType
        )
    }
}

private class PairCallback(
    private val session: Session,
    private val onResult: (success: Boolean, responder: WCResponder?) -> Unit,
    private val onConnect: () -> Unit,
) : Session.Callback {
    override fun onMethodCall(call: Session.MethodCall) {
        "Pair response:$call".log()
        if (call is Session.MethodCall.Response) {
            if (call.error != null) {
                dispatchResult(false)
            }
            try {
                (call.result as? Map<String, *>)?.let {
                    dispatchResult(true, (it["chainId"] as? Number)?.toLong())
                } ?: throw Error("parse error")
            } catch (e: Throwable) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                dispatchResult(false)
            }
        }
    }

    override fun onStatus(status: Session.Status) {
        // clear callbacks after approved or not
        "Pair Status:$status".log()
        if (status is Session.Status.Error) {
            status.throwable.printStackTrace()
        }
        when (status) {
            Session.Status.Approved -> {
                // wait for response,to get chainId
            }
            Session.Status.Closed, Session.Status.Disconnected, is Session.Status.Error -> {
                dispatchResult(false)
            }
            Session.Status.Connected -> {
                onConnect.invoke()
            }
        }
    }

    private fun dispatchResult(success: Boolean, chainId: Long? = null) {
        session.removeCallback(this)
        session.responder(chainId = chainId)?.let {
            onResult.invoke(success, it)
        } ?: onResult.invoke(false, null)
    }
}

private class ConnectedSessionCallback(
    private val session: Session,
    private val address: String,
    private val onDisconnect: (address: String, session: Session) -> Unit,
    private val onResponse: (id: Long, response: Any, error: Throwable?) -> Unit
) : Session.Callback {
    override fun onMethodCall(call: Session.MethodCall) {
        when (call) {
            is Session.MethodCall.Response -> {
                call.error?.let {
                    onResponse.invoke(call.id, "failed", WCError(errorCode = it.code.toString(), message = it.message))
                } ?: onResponse.invoke(call.id, call.result ?: "", null)
            }
            else -> {}
        }
    }

    override fun onStatus(status: Session.Status) {
        "$address Status:$status".log()
        if (status is Session.Status.Error && BuildConfig.DEBUG) {
            status.throwable.printStackTrace()
        }
        if (status == Session.Status.Closed) {
            session.clearCallbacks()
            onDisconnect.invoke(address, session)
        }
    }
}

private fun String.log() {
    if (BuildConfig.DEBUG) {
        Log.d("WalletConnectClient", this)
    }
}
