package com.dimension.maskbook.wallet.repository

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.DbWCWallet
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.services.model.WCSupportedWallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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
    fun isSupported(chainType: ChainType, context: Context): Boolean {
        val installed = try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return chains.contains(chainType)
                && installed
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
                chains = chains
            )
        }
    }

}

interface IWalletConnectRepository {
    val supportedWallets: Flow<List<WCWallet>>
    suspend fun refreshSupportedWallets()
}

class WalletConnectRepository(
    private val walletServices: WalletServices,
    private val database: AppDatabase
) : IWalletConnectRepository {

    override val supportedWallets: Flow<List<WCWallet>>
        get() = database.wcWalletDao().getAll().map {
            it.map { wallet -> WCWallet.fromDb(wallet) }
        }

    override suspend fun refreshSupportedWallets() {
        withContext(Dispatchers.IO) {
            walletServices.walletConnectServices.supportedWallets().map.values.let {
                it.mapNotNull { wallet ->
                    wallet.toDb()
                }
            }
        }
    }

    private fun WCSupportedWallet.toDb():DbWCWallet? {
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
                getChainType(it)
            } ?: emptyList()
        )
    }

    private fun getChainType(chain: String):ChainType? {
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