package com.dimension.maskbook.wallet.repository

import android.net.Uri
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.*
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.services.model.WCSupportedWallet
import com.dimension.maskbook.wallet.walletconnect.WCResponder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

data class WCWallet(
    val id: String,
    val name: String,
    val homePage: String,
    val nativeDeeplink: String,
    val universalLink: String,
    val shortName: String,
    val logo: String,
    val packageName: String,
    val chains: List<ChainType>
) {
    val displayName: String
        get() = if (shortName.isNotEmpty()) shortName else name

    fun isSupported(chainType: ChainType): Boolean {
        return chains.contains(chainType)
                && (nativeDeeplink.isNotEmpty() || universalLink.isNotEmpty())
    }

    fun wcDeeplink(wcUrl: String): String {
        return if (nativeDeeplink.isNotEmpty()) {
            "$nativeDeeplink//wc?uri=$wcUrl"
        } else {
            "${universalLink.toLinkPrefix()}wc?uri=$wcUrl"
        }
    }

    private fun String.toLinkPrefix() = if (endsWith("/")) this else "$this/"

    companion object {
        fun fromDb(wallet: DbWCWallet) = with(wallet) {
            WCWallet(
                id = id,
                name = name,
                homePage = homePage,
                nativeDeeplink = nativeDeeplink,
                universalLink = universalLink,
                shortName = shortName,
                logo = logo,
                packageName = packageName,
                chains = chains.map { ChainType.valueOf(it) }
            )
        }
    }

}

interface IWalletConnectRepository {
    val supportedWallets: Flow<List<WCWallet>>
    fun init()
    // returns id of first wallet
    suspend fun saveAccounts(responder: WCResponder, platformType: CoinPlatformType):String?
}

class WalletConnectRepository(
    private val walletServices: WalletServices,
    private val database: AppDatabase
) : IWalletConnectRepository {
    private val wcScope = CoroutineScope(Dispatchers.IO)

    override fun init() {
        wcScope.launch {
            try {
                refreshSupportedWallets()
            } catch (e: Throwable) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                //retry
                delay(30000)
                refreshSupportedWallets()
            }

        }
    }

    override suspend fun saveAccounts(
        responder: WCResponder,
        platformType: CoinPlatformType
    ): String? {
        val storedKeys = mutableListOf<DbStoredKey>()
        val wallets = responder.accounts.map { address ->
            val storedKey = DbStoredKey(
                id = UUID.randomUUID().toString(),
                hash = UUID.randomUUID().toString(),
                source = WalletSource.WalletConnect,
                data = byteArrayOf(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            ).also {
                storedKeys.add(it)
            }
            DbWallet(
                id = UUID.randomUUID().toString(),
                address = address,
                name = responder.name,
                storeKeyId = storedKey.id,
                derivationPath = "",
                extendedPublicKey = "",
                coin = "",
                platformType = platformType,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                walletConnectChainType = responder.chainType,
            )
        }

        database.storedKeyDao().add(storedKeys)
        database.walletDao().add(wallets)
        return wallets.firstOrNull()?.id
    }


    override val supportedWallets: Flow<List<WCWallet>>
        get() = database.wcWalletDao().getAll().map {
            it.map { wallet -> WCWallet.fromDb(wallet) }
        }

    suspend fun refreshSupportedWallets() {
        walletServices.walletConnectServices.supportedWallets().values.let {
            it.mapNotNull { wallet ->
                wallet.toDb()
            }
        }.let {
            database.wcWalletDao().add(it)
        }
    }

    private fun WCSupportedWallet.toDb(): DbWCWallet? {
        if (id.isNullOrEmpty()
            || app?.android.isNullOrEmpty()
        ) return null
        return DbWCWallet(
            id = id,
            name = name ?: "",
            homePage = homepage ?: "",
            nativeDeeplink = mobile?.native ?: "",
            universalLink = mobile?.universal ?: "",
            shortName = metadata?.shortName ?: "",
            logo = "https://registry.walletconnect.org/logo/sm/${id}.jpeg",
            packageName = app?.android?.let {
                try {
                    Uri.parse(it).getQueryParameter("id")
                } catch (e: Throwable) {
                    ""
                }
            } ?: "",
            chains = chains?.mapNotNull {
                getChainType(it)?.toString()
            } ?: emptyList()
        )
    }

    private fun getChainType(chain: String): ChainType? {
        //normally like eip155:1 eip155:56
        return when {
            chain.startsWith("eip155:") -> {
                val chainId = chain.split(":").last()
                ChainType.values().find {
                    it.chainId.toString() == chainId
                }
            }
            else -> null
        }
    }
}