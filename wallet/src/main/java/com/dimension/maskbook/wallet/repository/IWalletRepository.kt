package com.dimension.maskbook.wallet.repository

import com.dimension.maskbook.debankapi.model.ChainID
import com.dimension.maskbook.wallet.db.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import java.math.BigDecimal

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

data class WalletData(
    val id: String,
    val name: String,
    val address: String,
    val imported: Boolean,
    val fromWalletConnect: Boolean,
    val walletConnectChainType: ChainType? = ChainType.eth,
    val walletConnectDeepLink: String? = null,
    val tokens: List<WalletTokenData>,
    val balance: Map<DbWalletBalanceType, BigDecimal>,
) {
    companion object {
        fun fromDb(data: DbWalletTokenTokenWithWallet) = with(data) {
            WalletData(
                id = wallet.id,
                name = wallet.name,
                address = wallet.address,
                imported = storedKey.source == WalletSource.ImportedKeyStore || storedKey.source == WalletSource.ImportedMnemonic || storedKey.source == WalletSource.ImportedPrivateKey,
                fromWalletConnect = storedKey.source == WalletSource.WalletConnect,
                tokens = items.map {
                    WalletTokenData.fromDb(it)
                },
                balance = balance.map { it.type to it.value }.toMap(),
                walletConnectChainType = wallet.walletConnectChainType,
                walletConnectDeepLink = wallet.walletConnectDeepLink
            )
        }
    }
}

data class WalletTokenData(
    val count: BigDecimal,
    val tokenAddress: String,
    val tokenData: TokenData,
) {
    companion object {
        fun fromDb(data: DbWalletTokenWithToken) = with(data) {
            WalletTokenData(
                count = reference.count,
                tokenAddress = token.address,
                tokenData = TokenData.fromDb(token)
            )
        }
    }
}

enum class TransactionType {
    Swap,
    Receive,
    Send,
    Approve,
    Cancel
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
    val tokenData: TokenData,
)

data class SearchAddressResult(
    val query: String, // bind  result to query
    val success: Boolean,
    val errorMsg: String? = null,
    val data: ISearchAddressResultData? = null
)

interface ISearchAddressResultData

data class MultipleAddressResultData(
    val contacts: List<SearchAddressData>,
    val suggestions: List<SearchAddressData>
) : ISearchAddressResultData

data class SingleAddressResultData(
    val address: SearchAddressData
) : ISearchAddressResultData

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

@Serializable
enum class ChainType(
    val chainId: Long,
    val endpoint: String,
    val supportEip25519: Boolean,
) {
    eth(1, "https://mainnet.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", true),
    rinkeby(4, "https://rinkeby.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", true),
    bsc(56, "https://bsc-dataseed.binance.org", false),
    polygon(137, "https://polygon-mainnet.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", false),
    arbitrum(42_161, "https://arb1.arbitrum.io/rpc", false),
    xdai(100, "https://rpc.xdaichain.com", false),
    optimism(10, "https://mainnet.optimism.io", false),
    polka(1, "", false),// TODO: json rpc endpoint
    kovan(42, "https://kovan.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", true),
    goerli(5, "https://goerli.infura.io/v3/d74bd8586b9e44449cef131d39ceeefb", true),
    kusama(8, "https://kusama-rpc.polkadot.io/", false),
    westend(9, "https://westend-rpc.polkadot.io/", false),
    edgeware(10, "https://edgeware-node.edgewa.re/", false),
    polkadot(17, "https://polkadot-rpc.polkadot.io/", false),
    node(0, "", false),
    custom(0, "", false),
    unknown(0, "", false),
}

fun String.toChainType(): ChainType {
    return kotlin.runCatching { ChainType.valueOf(this) }.getOrNull() ?: ChainType.unknown
}

val ChainType.dbank: ChainID
    get() = when (this) {
        ChainType.eth -> ChainID.eth
        ChainType.rinkeby -> ChainID.eth
        ChainType.bsc -> ChainID.bsc
        ChainType.polygon -> ChainID.matic
        ChainType.arbitrum -> ChainID.arb
        ChainType.xdai -> ChainID.xdai
        ChainType.optimism -> ChainID.op
        ChainType.polka -> ChainID.eth
        else -> throw NotImplementedError("ChainType $this not supported")
    }

val ChainID.chainType: ChainType
    get() = when (this) {
        ChainID.eth -> ChainType.eth
        ChainID.bsc -> ChainType.bsc
        ChainID.matic -> ChainType.polygon
        ChainID.arb -> ChainType.arbitrum
        ChainID.xdai -> ChainType.xdai
        ChainID.op -> ChainType.optimism
        else -> ChainType.unknown
    }

data class DWebData(
    val coinPlatformType: CoinPlatformType,
    val chainType: ChainType,
)

data class SendTokenConfirmData(
    val data: SendTransactionData,
    val id: Any,
    val onDone: (String?) -> Unit,
    val onCancel: () -> Unit,
    val onError: (Throwable) -> Unit,
)

@Serializable
data class SendTransactionData(
    val from: String? = null,
    val to: String? = null,
    val value: String? = null,
    val gas: String? = null,
    val gasPrice: String? = null,
    val data: String? = null,
    val nonce: Long? = null,
    val chainId: Long? = null,
    val common: SendTransactionDataCommon? = null,
    val chain: String? = null,
    val hardfork: String? = null,
)

@Serializable
data class SendTransactionDataCommon(
    val customChain: CustomChainParams?,
    val baseChain: String?,
    val hardfork: String?,
)

@Serializable
data class CustomChainParams(
    val name: String?,
    val networkId: Long?,
    val chainId: Long?,
)

interface IWalletRepository {
    fun init()
    val dWebData: Flow<DWebData>
    fun setActiveCoinPlatformType(platformType: CoinPlatformType)
    fun setChainType(networkType: ChainType, notifyJS: Boolean = true)
    suspend fun findWalletByAddress(address: String): WalletData?
    val wallets: Flow<List<WalletData>>
    val currentWallet: Flow<WalletData?>
    fun setCurrentWallet(walletData: WalletData?)
    fun setCurrentWallet(walletId: String)
    fun generateNewMnemonic(): List<String>
    suspend fun createWallet(mnemonic: List<String>, name: String, platformType: CoinPlatformType)
    suspend fun importWallet(mnemonicCode: List<String>, name: String, path: List<String>, platformType: CoinPlatformType)
    suspend fun importWallet(name: String, keyStore: String, password: String, platformType: CoinPlatformType)
    suspend fun importWallet(name: String, privateKey: String, platformType: CoinPlatformType)
    suspend fun getKeyStore(walletData: WalletData, platformType: CoinPlatformType): String
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
        gasFee: BigDecimal,
        maxFee: Double,
        maxPriorityFee: Double,
        onDone: (String?) -> Unit = {},
        onError: (Throwable) -> Unit = {},
    )
    fun sendTokenWithCurrentWallet(
        amount: BigDecimal,
        address: String,
        tokenData: TokenData,
        gasLimit: Double,
        gasFee: BigDecimal,
        maxFee: Double,
        maxPriorityFee: Double,
        data: String,
        onDone: (String?) -> Unit = {},
        onError: (Throwable) -> Unit = {},
    )
    fun sendTokenWithCurrentWalletAndChainType(
        amount: BigDecimal,
        address: String,
        chainType: ChainType,
        gasLimit: Double,
        gasFee: BigDecimal,
        maxFee: Double,
        maxPriorityFee: Double,
        data: String,
        onDone: (String?) -> Unit,
        onError: (Throwable) -> Unit
    )
    fun validatePrivateKey(privateKey: String): Boolean
    fun validateMnemonic(mnemonic: String): Boolean
    fun validateKeystore(keyStore: String): Boolean
}

//class FakeWalletRepository : IWalletRepository {
//    override fun init() {
//
//    }
//
//    override val currentWallet: Flow<WalletData?> = MutableStateFlow(null)
//}

