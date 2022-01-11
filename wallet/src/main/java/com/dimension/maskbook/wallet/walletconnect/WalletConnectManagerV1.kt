package com.dimension.maskbook.wallet.walletconnect

import android.content.Context
import com.dimension.maskbook.wallet.services.okHttpClient
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableStateFlow
import org.komputing.khex.extensions.toNoPrefixHexString
import org.walletconnect.Session
import org.walletconnect.impls.FileWCSessionStore
import org.walletconnect.impls.MoshiPayloadAdapter
import org.walletconnect.impls.OkHttpTransport
import org.walletconnect.impls.WCSession
import java.io.File
import java.util.*

class WalletConnectManagerV1(private val context: Context):WalletConnectManager {
    private var session:Session? = null
    private var config: Session.Config? = null
    private val bridgeServer = "https://safe-walletconnect.gnosis.io"
    private val moshi by lazy {
        Moshi.Builder().build()
    }
    private val storage by lazy {
        FileWCSessionStore(File(context.cacheDir, "v1_session_store.json").apply {
            if (!this.exists()) createNewFile()
        }, moshi)
    }
    private var setUp = false

    override fun setUp() {
        if (setUp) return
        setUp = true
        session?.clearCallbacks()
        config = Session.Config(
            handshakeTopic = UUID.randomUUID().toString(),
            bridge = bridgeServer,
            key = ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
        )
        config?.let {
            session = WCSession(
                config = it,
                // TODO replace to serialize
                payloadAdapter = MoshiPayloadAdapter(moshi),
                sessionStore = storage,
                transportBuilder = OkHttpTransport.Builder(client = okHttpClient, moshi = moshi),
                clientMeta = Session.PeerMeta(name = "MaskNetwork")
            ).apply {
                offer()
            }
            _wcUrl.value = it.toWCUri()
        } ?: run {
            setUp = false
            throw WCErrorV1.configError()
        }
    }

    override fun shutDown() {
        session?.clearCallbacks()
        session?.kill()
        setUp = false
    }

    private val _wcUrl = MutableStateFlow("")
    override val wcUrl
        get() = _wcUrl
}

class WCErrorV1(msg: String):Error(msg) {
    companion object{
       fun setUpNeeded() = WCErrorV1("setUp() haven't been called!")
       fun configError() = WCErrorV1("can't generate Session.Config!")
    }
}