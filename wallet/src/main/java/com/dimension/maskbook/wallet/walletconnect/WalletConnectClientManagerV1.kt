package com.dimension.maskbook.wallet.walletconnect

import android.content.Context
import android.util.Log
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.ext.ether
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.services.okHttpClient
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import org.komputing.khex.extensions.toNoPrefixHexString
import org.walletconnect.Session
import org.walletconnect.impls.FileWCSessionStore
import org.walletconnect.impls.MoshiPayloadAdapter
import org.walletconnect.impls.OkHttpTransport
import java.io.File
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class WalletConnectClientManagerV1(private val context: Context) : WalletConnectClientManager {
    private var config: Session.Config? = null
    private var session: Session? = null
    private val bridgeServer = "https://safe-walletconnect.gnosis.io"
    private var onDisconnect: (address: String) -> Unit = {}
    private val connectedSessions = ConcurrentHashMap<String, Session>()
    private val moshi by lazy {
        Moshi.Builder().build()
    }

    private val storage by lazy {
        FileWCSessionStore(File(context.cacheDir, "v1_session_store.json").apply {
            if (!this.exists()) createNewFile()
        }, moshi)
    }

    private val _wcUrl = MutableStateFlow("")
    override val wcUrl: Flow<String>
        get() = _wcUrl.onEach {
            Log.d("Mimao", "Current WcUrl:$it")
        }

    override fun connect(onResult: (success: Boolean, responder: WCResponder?) -> Unit) {
        session?.clearCallbacks()
        if (session == null) {
            Session.Config(
                handshakeTopic = UUID.randomUUID().toString(),
                bridge = bridgeServer,
                key = ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
            ).let {
                config = it
                session = it.session().apply {
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
        Log.d("Mimao", "resetSession")
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
            Log.d("Mimao", "Stored state:$it")
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
        gasPrice: BigDecimal
    ) {
        connectedSessions[fromAddress]?.let {
            it.performMethodCall(
                Session.MethodCall.SendTransaction(
                    id = System.currentTimeMillis(),
                    from = fromAddress,
                    to = toAddress,
                    nonce = "",
                    gasPrice = "0x${gasPrice.ether.also { 
                        Log.d("Mimao", "gas price is ${it.gwei}, total is:${gasLimit * it.gwei.toLong()}")
                    }.wei.toBigInteger().toString(16)}",
                    gasLimit = gasLimit.toString(),//"0x${gasLimit.toBigDecimal().toBigInteger().toString(16)}",
                    data = data,
                    value = "0x${amount.ether.wei.toLong().toBigInteger().toString(16)}"
                ).also {
                       Log.d("Mimao", "send params:$it")
                },
                callback = {
                    Log.d("Mimao", "transaction response:$it")
                }
            )
        }
    }

    private fun addToConnected(session: Session, handshakeTopic: String) {
        session.approvedAccounts()?.forEach {
            connectedSessions[it] = session.apply {
                addCallback(ConnectedSessionCallback(this, it, onDisconnect = { address, _ ->
                    // remove from connected sessions
                    connectedSessions.remove(address)
                    this@WalletConnectClientManagerV1.onDisconnect.invoke(address)
                }))
                update(
                    approvedAccounts() ?: emptyList(),
                    chainId = storage.load(handshakeTopic)?.chainId ?: -1
                )
            }
        }
    }

    private fun Session.Config.session() = WCSessionV1(
        config = this,
        payloadAdapter = MoshiPayloadAdapter(moshi),
        sessionStore = storage,
        transportBuilder = OkHttpTransport.Builder(client = okHttpClient, moshi = moshi),
        clientMeta = Session.PeerMeta(
            name = "Mask Network",
            url = "https://mask.io",
            description = "Mask Network"
        )
    )
}


private fun Session.responder(chainId: Long?) = peerMeta()?.let {
    Log.d("Mimao", "responder chain id:$chainId")
    chainId?.let { id ->
        ChainType.values().find { type ->
            Log.d("Mimao", "chaind id :${type.chainId}, wc id:$id")
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
        Log.d("Mimao", "resonposne:$call")
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
        //clear callbacks after
        Log.d("Mimao", "Status:$status, ${session.approvedAccounts()}, ${session.peerMeta()}")
        if (status is Session.Status.Error) {
            status.throwable.printStackTrace()
        }
        when (status) {
            Session.Status.Approved -> {
                //wait for response,to get chainId
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
) : Session.Callback {
    override fun onMethodCall(call: Session.MethodCall) {
        // do nothing
    }

    override fun onStatus(status: Session.Status) {
        Log.d("Mimao", "Stored State Changed:$status")
        if (status is Session.Status.Error) {
            status.throwable.printStackTrace()
        }
        if (status == Session.Status.Disconnected) {
            session.clearCallbacks()
            onDisconnect.invoke(address, session)
        }
    }
}