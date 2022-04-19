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

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.ifNullOrEmpty
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.db.model.DbCollectible
import com.dimension.maskbook.wallet.db.model.DbWalletTokenTokenWithWallet
import com.dimension.maskbook.wallet.db.model.DbWalletTokenWithToken
import com.dimension.maskbook.wallet.db.model.WalletSource
import com.dimension.maskbook.wallet.export.model.BackupWalletData
import com.dimension.maskbook.wallet.export.model.ChainData
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleCollectionData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleContract
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.export.model.WalletData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import kotlinx.coroutines.flow.Flow

data class WalletCreateOrImportResult(
    val type: Type,
    val wallet: WalletData? = null,
    val title: String? = null, // Error, Create Wallet Success...
    val message: String? = null
) {
    enum class Type {
        SUCCESS,
        ERROR,
        WARNING // e.g wallet already exists
    }
}

fun WalletData.Companion.fromDb(data: DbWalletTokenTokenWithWallet) = with(data) {
    WalletData(
        id = wallet.id,
        name = wallet.name,
        address = wallet.address,
        imported = storedKey.source == WalletSource.ImportedKeyStore || storedKey.source == WalletSource.ImportedMnemonic || storedKey.source == WalletSource.ImportedPrivateKey,
        fromWalletConnect = storedKey.source == WalletSource.WalletConnect,
        tokens = items.map {
            WalletTokenData.fromDb(it)
        },
        balance = balance.associate { it.type to it.value },
        walletConnectChainType = wallet.walletConnectChainType,
        walletConnectDeepLink = wallet.walletConnectDeepLink
    )
}

fun WalletTokenData.Companion.fromDb(data: DbWalletTokenWithToken) = with(data) {
    WalletTokenData(
        count = reference.count,
        tokenAddress = token.address,
        tokenData = TokenData.fromDb(token)
    )
}

fun WalletCollectibleData.Companion.fromDb(data: DbCollectible) = with(data) {
    WalletCollectibleData(
        id = _id,
        chainType = chainType,
        name = name,
        icon = collection.imageURL.ifNullOrEmpty { url.imagePreviewURL ?: "" },
        tokenId = tokenId,
        link = permalink ?: externalLink ?: "",
        imageUrl = url.imageURL ?: url.imageOriginalURL ?: "",
        previewUrl = url.imagePreviewURL ?: url.imageThumbnailURL ?: "",
        videoUrl = url.animationOriginalURL ?: url.animationURL ?: "",
        contract = WalletCollectibleContract(
            address = contract.address,
            imageUrl = contract.imageUrl,
            name = contract.name,
            symbol = contract.symbol,
            schema = contract.schema,
        ),
        collection = WalletCollectibleCollectionData.fromDb(this)
    )
}

fun WalletCollectibleCollectionData.Companion.fromDb(data: DbCollectible) = with(data) {
    WalletCollectibleCollectionData(
        slug = collection.slug,
        name = collection.name ?: "",
        imageUrl = collection.imageURL ?: "",
        walletId = walletId,
        chainType = chainType
    )
}

enum class TransactionType {
    Swap,
    Receive,
    Send,
    Approve,
    Cancel,
    Unknown,
}

enum class TransactionStatus {
    Success,
    Failure,
    Pending,
}

data class TransactionData(
    val id: String,
    val type: TransactionType,
    val count: BigDecimal,
    val status: TransactionStatus,
    val message: String,
    val createdAt: Long,
    val updatedAt: Long,
    val tokenData: TransactionTokenData,
)

data class TransactionTokenData(
    val id: String,
    val contractId: String,
    val symbol: String,
    val price: BigDecimal,
    val chainType: ChainType
)

enum class GasPriceEditMode {
    LOW,
    MEDIUM,
    HIGH,
    CUSTOM
}

enum class UnlockType {
    PASSWORD,
    BIOMETRIC
}

data class DWebData(
    val coinPlatformType: CoinPlatformType,
    val chainType: ChainType,
)

interface IWalletRepository {
    suspend fun init()
    val dWebData: Flow<DWebData>
    fun setActiveCoinPlatformType(platformType: CoinPlatformType)
    fun setChainType(networkType: ChainType, notifyJS: Boolean = true)
    suspend fun findWalletByAddress(address: String): WalletData?
    val wallets: Flow<List<WalletData>>
    val currentWallet: Flow<WalletData?>
    val currentChain: Flow<ChainData?>
    suspend fun getChainTokenData(chainType: ChainType): ChainData?
    fun setCurrentWallet(walletData: WalletData?)
    fun setCurrentWallet(walletId: String)
    fun generateNewMnemonic(): List<String>
    suspend fun createWallet(mnemonic: List<String>, name: String, platformType: CoinPlatformType)
    suspend fun importWallet(
        mnemonicCode: List<String>,
        name: String,
        path: List<String>,
        platformType: CoinPlatformType
    )

    suspend fun importWallet(
        name: String,
        keyStore: String,
        password: String,
        platformType: CoinPlatformType
    )

    suspend fun importWallet(name: String, privateKey: String, platformType: CoinPlatformType)
    suspend fun getKeyStore(
        walletData: WalletData,
        platformType: CoinPlatformType,
        paymentPassword: String
    ): String

    suspend fun getPrivateKey(walletData: WalletData, platformType: CoinPlatformType): String
    suspend fun getTotalBalance(address: String): Double
    fun deleteCurrentWallet()
    fun deleteWallet(id: String)
    fun renameWallet(value: String, id: String)
    fun renameCurrentWallet(value: String)
    fun sendTokenWithCurrentWallet(
        amount: BigDecimal,
        address: String,
        tokenData: TokenData,
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double,
        onDone: (String?) -> Unit = {},
        onError: (Throwable) -> Unit = {},
    )

    fun sendCollectibleWithCurrentWallet(
        address: String,
        collectible: WalletCollectibleData,
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double,
        onDone: (String?) -> Unit = {},
        onError: (Throwable) -> Unit = {},
    )

    fun sendTransactionWithCurrentWallet(
        amount: BigDecimal,
        address: String,
        chainType: ChainType,
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double,
        data: String,
        onDone: (String?) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun validatePrivateKey(privateKey: String): Boolean
    fun validateMnemonic(mnemonic: String): Boolean
    fun validateKeystore(keyStore: String): Boolean
    suspend fun getEnsAddress(chainType: ChainType, name: String): String
    suspend fun getChainData(chainType: ChainType): Flow<ChainData?>
    suspend fun refreshWallet()
    suspend fun signMessage(message: String, fromAddress: String): String?
    suspend fun createWalletBackup(): List<BackupWalletData>
    suspend fun restoreWalletBackup(wallet: List<BackupWalletData>)
}
