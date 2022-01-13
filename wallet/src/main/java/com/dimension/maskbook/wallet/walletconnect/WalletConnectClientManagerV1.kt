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

class WalletConnectClientManagerV1(private val context: Context) : WalletConnectClientManager {
    private var config: Session.Config? = null
    private var session: Session? = null
    private val bridgeServer = "https://safe-walletconnect.gnosis.io"
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

    override fun connect(onResult: (success: Boolean, wcUrl: String, approvedAccounts: List<String>) -> Unit) {
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
                            _wcUrl.value,
                            approvedAccounts() ?: emptyList()
                        )
                        if (!success) {
                            kill()
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

    override fun disConnect(wcUrl: String) {
        wcUrl.session().apply {
            kill()
            clearCallbacks()
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

    private fun String.session() = Session.Config.fromWCUri(this).session()


    private class PairCallback(
        private val session: Session,
        private val onResult: (success: Boolean) -> Unit,
        private val onConnect: () -> Unit,
    ) : Session.Callback {
        override fun onMethodCall(call: Session.MethodCall) {
            // do nothing
            Log.d("Mimao", "resonposne:$call")
            if (call is Session.MethodCall.Response && call.error != null) {
                dispatchResult(false)
            }
        }

        override fun onStatus(status: Session.Status) {
            //clear callbacks after
            Log.d("Mimao", "Status:$status")
            if (status is Session.Status.Error) {
                status.throwable.printStackTrace()
            }
            when (status) {
                Session.Status.Approved -> {
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
}