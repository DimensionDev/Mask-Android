package com.dimension.maskbook.wallet.walletconnect

import android.content.Context
import android.util.Log
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
import org.walletconnect.impls.WCSession
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

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
                    onResult = { success ->
                        onResult.invoke(
                            success,
                            responder()
                        )
                        if (success) {
                            session?.let {
                                addToConnected(it, config?.handshakeTopic ?: "")
                            }
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

    private fun Session.Config.session() = WCSession(
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


private fun Session.responder() = peerMeta()?.let {
    WCResponder(
        accounts = approvedAccounts() ?: emptyList(),
        name = it.name ?: "",
        description = it.description ?: "",
        icons = it.icons ?: emptyList(),
        url = it.url ?: ""
    )
}

private class PairCallback(
    private val session: Session,
    private val onResult: (success: Boolean) -> Unit,
    private val onConnect: () -> Unit,
) : Session.Callback {
    override fun onMethodCall(call: Session.MethodCall) {
        Log.d("Mimao", "resonposne:$call")
        if (call is Session.MethodCall.Response && call.error != null) {
            dispatchResult(false)
            // TODO parse response to get infomations:Response(id=1642063871762736, result={approved=true, chainId=4.0, networkId=0.0, accounts=[0xE08D12FEACe8B0a59828F72a9D691C430FB3B041], rpcUrl=, peerId=cd756507-f22f-4446-b651-7e3e17d0ccbf, peerMeta={description=MetaMask Mobile app, url=https://metamask.io, icons=[https://raw.githubusercontent.com/MetaMask/brand-resources/master/SVG/metamask-fox.svg], name=MetaMask, ssl=true}}, error=null)
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
                dispatchResult(true)
            }
            Session.Status.Closed, Session.Status.Disconnected, is Session.Status.Error -> {
                dispatchResult(false)
            }
            Session.Status.Connected -> {
                onConnect.invoke()
            }
        }
    }

    private fun dispatchResult(success: Boolean) {
        session.removeCallback(this)
        onResult.invoke(success)
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
        if (status == Session.Status.Disconnected || status == Session.Status.Closed) {
            session.clearCallbacks()
            onDisconnect.invoke(address, session)
        }
    }
}