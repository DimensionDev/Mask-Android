package com.dimension.maskbook.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.withTransaction
import com.dimension.maskbook.debankapi.model.ChainID
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.db.model.DbStoredKey
import com.dimension.maskbook.wallet.db.model.DbToken
import com.dimension.maskbook.wallet.db.model.DbWallet
import com.dimension.maskbook.wallet.db.model.DbWalletBalance
import com.dimension.maskbook.wallet.db.model.DbWalletBalanceType
import com.dimension.maskbook.wallet.db.model.DbWalletToken
import com.dimension.maskbook.wallet.db.model.WalletSource
import com.dimension.maskbook.wallet.ext.ether
import com.dimension.maskbook.wallet.ext.gwei
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.DWebData
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.TokenData
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.repository.dbank
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.services.okHttpClient
import com.dimension.maskwalletcore.WalletKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import java.math.BigDecimal
import java.util.UUID
import kotlin.math.pow
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime


private val CurrentCoinPlatformTypeKey = stringPreferencesKey("coin_platform_type")
private val CurrentWalletKey = stringPreferencesKey("current_wallet")
private val ChainTypeKey = stringPreferencesKey("chain_type")
val Context.walletDataStore: DataStore<Preferences> by preferencesDataStore(name = "wallet")

class WalletRepository(
    private val dataStore: DataStore<Preferences>,
    private val database: AppDatabase,
    private val services: WalletServices,
) : IWalletRepository {
    private val tokenScope = CoroutineScope(Dispatchers.IO)
    private val scope = CoroutineScope(Dispatchers.IO)

    @OptIn(ExperimentalTime::class)
    override fun init() {
        tokenScope.launch {
            while (true) {
                delay(12.seconds)
                refreshCurrentWalletToken()
            }
        }
    }

    override val dWebData: Flow<DWebData>
        get() = dataStore.data.map {
            val coinPlatformType = it[CurrentCoinPlatformTypeKey]?.let {
                CoinPlatformType.valueOf(it)
            } ?: CoinPlatformType.Ethereum
            val chainType = it[ChainTypeKey]?.let { ChainType.valueOf(it) } ?: ChainType.eth
            DWebData(
                coinPlatformType,
                chainType
            )
        }

    override fun setActiveCoinPlatformType(platformType: CoinPlatformType) {
        scope.launch {
            dataStore.edit {
                it[CurrentCoinPlatformTypeKey] = platformType.name
            }
        }
    }

    override fun setChainType(networkType: ChainType, notifyJS: Boolean) {
        scope.launch {
            dataStore.edit {
                it[ChainTypeKey] = networkType.name
            }
            if (notifyJS) {
                JSMethod.Wallet.updateEthereumChainId(networkType.chainId)
            }
        }
    }

    override suspend fun findWalletByAddress(address: String): WalletData? {
        return database.walletDao().getByAddress(address)?.let { WalletData.fromDb(it) }
    }

    private suspend fun refreshCurrentWalletToken() {
        val currentWallet = currentWallet.firstOrNull() ?: return
        val currentNetwork = dWebData.firstOrNull()?.chainType ?: return
        try {
            val token = services.debankServices.tokenList(
                currentWallet.address,
                currentNetwork.dbank,
                is_all = true,
                has_balance = false
            ).filter { it.isVerified == true }
            val balance =
                services.debankServices.totalBalance(currentWallet.address).let { balance ->
                    balance.chainList?.map { chain ->
                        chain.id?.let { it1 -> runCatching { ChainID.valueOf(it1) }.getOrNull() }
                            ?.let {
                                when (it) {
                                    ChainID.eth -> DbWalletBalanceType.eth
                                    ChainID.bsc -> DbWalletBalanceType.bsc
                                    ChainID.xdai -> DbWalletBalanceType.xdai
                                    ChainID.matic -> DbWalletBalanceType.polygon
                                    ChainID.op -> DbWalletBalanceType.optimism
                                    ChainID.arb -> DbWalletBalanceType.arbitrum
                                    else -> null
                                }
                            }?.let {
                                DbWalletBalance(
                                    UUID.randomUUID().toString(),
                                    currentWallet.id,
                                    it,
                                    chain.usdValue?.toBigDecimal() ?: BigDecimal.ZERO,
                                )
                            }
                    }?.mapNotNull { it }?.let {
                        it + listOf(
                            DbWalletBalance(
                                UUID.randomUUID().toString(),
                                currentWallet.id,
                                DbWalletBalanceType.all,
                                balance.totalUsdValue?.toBigDecimal() ?: BigDecimal.ZERO,
                            )
                        )
                    } ?: emptyList()
                }

            val tokens = token.map {
                DbToken(
                    id = it.id ?: "",
                    address = it.id ?: "",
                    chainId = it.chain ?: "eth",
                    name = it.name ?: "",
                    symbol = it.symbol ?: "",
                    decimals = it.decimals ?: 0L,
                    logoURI = it.logoURL,
                    price = BigDecimal(it.price ?: 0.0)
                )
            }
            val walletTokens = token.map {
                DbWalletToken(
                    id = UUID.randomUUID().toString(),
                    walletId = currentWallet.id,
                    count = BigDecimal(it.amount ?: 0.0),
                    tokenId = it.id ?: ""
                )
            }
            database.withTransaction {
                database.walletBalanceDao().add(balance)
                database.tokenDao().add(tokens)
                database.walletTokenDao().add(walletTokens)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override val wallets: Flow<List<WalletData>>
        get() = database
            .walletDao()
            .getAllFlow()
            .map { list ->
                list.map {
                    WalletData.fromDb(it)
                }
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentWallet: Flow<WalletData?>
        get() = dataStore.data.map {
            it[CurrentWalletKey]
        }.mapNotNull { it }.flatMapLatest {
            database.walletDao().getByIdFlow(it)
        }.map {
            it?.let { it1 -> WalletData.fromDb(it1) }
        }

    override fun setCurrentWallet(walletData: WalletData?) {
        scope.launch {
            val wallet = if (walletData != null) {
                database.walletDao().getById(walletData.id)?.wallet
            } else null
            setCurrentWallet(dbWallet = wallet)
        }
    }

    fun setCurrentWallet(dbWallet: DbWallet?) {
        scope.launch {
            dataStore.edit {
                it[CurrentWalletKey] = dbWallet?.id.orEmpty()
            }
            JSMethod.Wallet.updateEthereumAccount(dbWallet?.address.orEmpty())
        }
    }

    override fun generateNewMnemonic(): List<String> {
        return createNewMnemonic().split(" ")
    }

    override suspend fun createWallet(
        mnemonic: List<String>,
        name: String,
        platformType: CoinPlatformType
    ) {
        val wallet = WalletKey.fromMnemonic(mnemonic = mnemonic.joinToString(" "), "")
        val account = wallet.addNewAccountAtPath(
            platformType.coinType,
            platformType.derivationPath.toString(),
            name,
            ""
        )
        val storeKey = DbStoredKey(
            id = UUID.randomUUID().toString(),
            hash = wallet.hash,
            source = WalletSource.Created,
            data = wallet.data,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        val db = DbWallet(
            id = UUID.randomUUID().toString(),
            address = account.address,
            name = name,
            storeKeyId = storeKey.id,
            derivationPath = account.derivationPath,
            extendedPublicKey = account.extendedPublicKey,
            coin = account.coin,
            platformType = platformType,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        database.storedKeyDao().add(listOf(storeKey))
        database.walletDao().add(listOf(db))
        setCurrentWallet(db)
    }

    override suspend fun importWallet(
        mnemonicCode: List<String>,
        name: String,
        path: List<String>,
        platformType: CoinPlatformType
    ) {
        scope.launch {
            val wallet = WalletKey.fromMnemonic(mnemonic = mnemonicCode.joinToString(" "), "")
            val accounts = path.map {
                wallet.addNewAccountAtPath(platformType.coinType, it, name, "")
            }
            val storeKey = DbStoredKey(
                id = UUID.randomUUID().toString(),
                hash = wallet.hash,
                source = WalletSource.ImportedMnemonic,
                data = wallet.data,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
            val dbWallets = accounts.map { account ->
                DbWallet(
                    id = UUID.randomUUID().toString(),
                    address = account.address,
                    name = name,
                    storeKeyId = storeKey.id,
                    derivationPath = account.derivationPath,
                    extendedPublicKey = account.extendedPublicKey,
                    coin = account.coin,
                    platformType = platformType,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                )
            }
            database.storedKeyDao().add(listOf(storeKey))
            database.walletDao().add(dbWallets)
            dbWallets.firstOrNull()?.let { setCurrentWallet(it) }
        }
    }

    override suspend fun importWallet(
        name: String,
        keyStore: String,
        password: String,
        platformType: CoinPlatformType
    ) {
        scope.launch {
            val wallet = WalletKey.fromJson(
                json = keyStore,
                name = name,
                coinType = platformType.coinType,
                password = "",
                keyStoreJsonPassword = password
            )
            val account = wallet.addNewAccountAtPath(
                platformType.coinType,
                platformType.derivationPath.toString(),
                name,
                ""
            )
            val storeKey = DbStoredKey(
                id = UUID.randomUUID().toString(),
                hash = wallet.hash,
                source = WalletSource.ImportedKeyStore,
                data = wallet.data,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
            val db = DbWallet(
                id = UUID.randomUUID().toString(),
                address = account.address,
                name = name,
                storeKeyId = storeKey.id,
                derivationPath = account.derivationPath,
                extendedPublicKey = account.extendedPublicKey,
                coin = account.coin,
                platformType = platformType,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
            database.storedKeyDao().add(listOf(storeKey))
            database.walletDao().add(listOf(db))
            setCurrentWallet(db)
        }
    }

    override suspend fun importWallet(
        name: String,
        privateKey: String,
        platformType: CoinPlatformType
    ) {
        scope.launch {
            val wallet = WalletKey.fromPrivateKey(
                privateKey = privateKey,
                name = name,
                coinType = platformType.coinType,
                password = "",
            )
            val account =
                wallet.addNewAccountAtPath(
                    platformType.coinType,
                    platformType.derivationPath.toString(),
                    name,
                    ""
                )
            val storeKey = DbStoredKey(
                id = UUID.randomUUID().toString(),
                hash = wallet.hash,
                source = WalletSource.ImportedPrivateKey,
                data = wallet.data,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
            val db = DbWallet(
                id = UUID.randomUUID().toString(),
                address = account.address,
                name = name,
                storeKeyId = storeKey.id,
                derivationPath = account.derivationPath,
                extendedPublicKey = account.extendedPublicKey,
                coin = account.coin,
                platformType = platformType,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
            database.storedKeyDao().add(listOf(storeKey))
            database.walletDao().add(listOf(db))
            setCurrentWallet(db)
        }
    }

    override suspend fun getKeyStore(
        walletData: WalletData,
        platformType: CoinPlatformType
    ): String {
        return database.walletDao().getById(walletData.id)?.let {
            WalletKey.load(it.storedKey.data).firstOrNull()
        }?.exportKeyStoreJsonOfAddress(platformType.coinType, walletData.address, "", "") ?: ""
    }

    override suspend fun getPrivateKey(
        walletData: WalletData,
        platformType: CoinPlatformType
    ): String {
        return database.walletDao().getById(walletData.id)?.let {
            WalletKey.load(it.storedKey.data).firstOrNull()
        }?.exportPrivateKey(platformType.coinType, "") ?: ""
    }

    override suspend fun getTotalBalance(address: String): Double {
        return services.debankServices.totalBalance(address).totalUsdValue ?: 0.0
    }

    override fun deleteCurrentWallet() {
        scope.launch {
            val currentWallet = currentWallet.firstOrNull() ?: return@launch
            deleteWallet(currentWallet.id)
        }
    }

    override fun deleteWallet(id: String) {
        scope.launch {
            // get it before remove
            val currentWallet = currentWallet.firstOrNull()

            val tokenWallet = database.walletDao().getById(id) ?: return@launch
            database.walletDao().deleteById(tokenWallet.wallet.id)
            database.storedKeyDao().deleteById(tokenWallet.storedKey.id)
            database.walletBalanceDao().deleteByWalletId(tokenWallet.wallet.id)
            database.walletTokenDao().deleteByWalletId(tokenWallet.wallet.id)

            if (currentWallet?.id == id) {
                val next = database.walletDao().getAll().firstOrNull { it.wallet.id != id }
                setCurrentWallet(next?.wallet)
            }
        }
    }

    override fun renameWallet(value: String, id: String) {
        scope.launch {
            database.walletDao().getById(id)?.wallet?.copy(name = value)?.let {
                database.walletDao().add(listOf(it))
            }
        }
    }

    override fun renameCurrentWallet(value: String) {
        scope.launch {
            currentWallet.firstOrNull()?.let { wallet ->
                database.walletDao().getById(wallet.id)?.wallet
            }?.copy(name = value)?.let {
                database.walletDao().add(listOf(it))
            }
        }
    }

    override fun sendTokenWithCurrentWallet(
        amount: BigDecimal,
        address: String,
        tokenData: TokenData,
        gasLimit: Double,
        gasFee: BigDecimal,
        maxFee: Double,
        maxPriorityFee: Double,
        data: String,
        onDone: (String?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        runCatching { ChainType.valueOf(tokenData.chainId) }.getOrNull()
            ?.let { chainType ->
                sendTokenWithCurrentWalletAndChainType(
                    amount = amount,
                    address = address,
                    chainType = chainType,
                    gasLimit = gasLimit,
                    gasFee = gasFee,
                    maxFee = maxFee,
                    maxPriorityFee = maxPriorityFee,
                    onDone = onDone,
                    onError = onError,
                    data = data,
                )
            }
    }


    override fun sendTokenWithCurrentWalletAndChainType(
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
    ) {
        scope.launch {
            try {
                val hash = currentWallet.firstOrNull()?.let { wallet ->
                    val credentials = Credentials.create(
                        getPrivateKey(
                            wallet,
                            CoinPlatformType.Ethereum
                        )
                    )
                    val actualAmount = if (chainType == ChainType.eth) {
                        amount.ether.wei.toBigInteger()
                    } else {
                        null
                    }
                    val web3 = Web3j.build(HttpService(chainType.endpoint, okHttpClient))
                    val manager =
                        RawTransactionManager(web3, credentials, chainType.chainId)

                    val result = if (chainType.supportEip25519) {
                        manager.sendEIP1559Transaction(
                            chainType.chainId,
                            maxPriorityFee.gwei.wei.toBigInteger(),
                            maxFee.gwei.wei.toBigInteger(),
                            gasLimit.toBigDecimal().toBigInteger(),
                            address,
                            data,
                            actualAmount,
                        )
                    } else {
                        manager.sendTransaction(
                            maxPriorityFee.gwei.wei.toBigInteger(),
                            gasLimit.toBigDecimal().toBigInteger(),
                            address,
                            data,
                            amount.ether.wei.toBigInteger()
                        )
                    }
                    if (result.hasError()) {
                        Log.e(
                            "WalletRepository",
                            "sendTokenWithCurrentWallet: ${result.error?.code}: ${result.error?.message}",
                        )
                        Log.e(
                            "WalletRepository",
                            "sendTokenWithCurrentWallet: ${result.error?.data}",
                        )
                        throw Exception(result.error?.message ?: "")
                    }
                    result.transactionHash
                }
                onDone.invoke(hash)
            } catch (e: Throwable) {
                onError(e)
            }
        }
    }

    override fun validatePrivateKey(privateKey: String) = WalletKey.validate(privateKey = privateKey)

    override fun validateMnemonic(mnemonic: String) =  WalletKey.validate(mnemonic = mnemonic)

    override fun validateKeystore(keyStore: String) = WalletKey.validate(keyStoreJSON = keyStore)

    override fun sendTokenWithCurrentWallet(
        amount: BigDecimal,
        address: String,
        tokenData: TokenData,
        gasLimit: Double,
        gasFee: BigDecimal,
        maxFee: Double,
        maxPriorityFee: Double,
        onDone: (String?) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        val data = Function(
            "transfer",
            listOf(
                Address(address),
                Uint256((amount * (10.0.pow(tokenData.decimals.toInt())).toBigDecimal()).toBigInteger())
            ),
            listOf(),
        ).let {
            FunctionEncoder.encode(it)
        }
        sendTokenWithCurrentWallet(
            amount = amount,
            address = address,
            tokenData = tokenData,
            gasLimit = gasLimit,
            gasFee = gasFee,
            maxFee = maxFee,
            maxPriorityFee = maxPriorityFee,
            data = data,
            onDone = onDone,
            onError = onError
        )
    }

    private suspend fun getWalletKey(walletData: WalletData): WalletKey? {
        return database.walletDao().getById(walletData.id)?.storedKey?.data?.let {
            WalletKey.load(it)
        }?.firstOrNull()
    }

    private fun createNewMnemonic(password: String = ""): String {
        return WalletKey.create(password).mnemonic
    }
}