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
package com.dimension.maskbook.wallet.repository

import android.net.Uri
import androidx.room.withTransaction
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.db.model.DbStoredKey
import com.dimension.maskbook.wallet.db.model.DbWCWallet
import com.dimension.maskbook.wallet.db.model.DbWallet
import com.dimension.maskbook.wallet.db.model.WalletSource
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.services.model.WCSupportedWallet
import com.dimension.maskbook.wallet.walletconnect.WCResponder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

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
        return chains.contains(chainType) &&
            (nativeDeeplink.isNotEmpty() || universalLink.isNotEmpty())
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
    suspend fun saveAccounts(responder: WCResponder, platformType: CoinPlatformType): String?
}

class WalletConnectRepository(
    private val walletServices: WalletServices,
    private val database: AppDatabase,
    private val scope: CoroutineScope,
) : IWalletConnectRepository {

    override fun init() {
        scope.launch {
            try {
                refreshSupportedWallets()
            } catch (e: Throwable) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                // retry
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
                walletConnectDeepLink = supportedWallets.firstOrNull()?.find {
                    try {
                        Uri.parse(it.homePage).host.equals(Uri.parse(responder.url).host, ignoreCase = true)
                    } catch (e: Throwable) {
                        false
                    }
                }?.nativeDeeplink
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
            database.withTransaction {
                // some wallets might have been changed with a different id
                database.wcWalletDao().clearAll()
                database.wcWalletDao().add(it)
            }
        }
    }

    private fun WCSupportedWallet.toDb(): DbWCWallet? {
        if (id.isNullOrEmpty() ||
            app?.android.isNullOrEmpty()
        ) return null
        return DbWCWallet(
            id = id,
            name = name ?: "",
            homePage = homepage ?: "",
            nativeDeeplink = mobile?.native ?: "",
            universalLink = mobile?.universal ?: "",
            shortName = metadata?.shortName ?: "",
            logo = "https://registry.walletconnect.org/logo/md/$id.jpeg",
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
        // normally like eip155:1 eip155:56
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
