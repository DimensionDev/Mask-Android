package com.dimension.maskbook.wallet.db.model

import androidx.room.*
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.model.DerivationPath
import com.dimension.maskwalletcore.CoinType

enum class WalletSource {
    Created,
    ImportedPrivateKey,
    ImportedKeyStore,
    ImportedMnemonic,
    WalletConnect,
}

enum class CoinPlatformType(
    val derivationPath: DerivationPath,
    val coinType: CoinType,
    val coinId: Int,
) {
    Ethereum(
        derivationPath = DerivationPath(44, 60, 0, 0, 0),
        coinType = CoinType.Ethereum,
        coinId = 60,
    ),
    Polkadot(
        derivationPath = DerivationPath(44, 0, 0, 0, 0),
        coinType = CoinType.Polkadot,
        coinId = -1,// TODO
    ),
}

@Entity
data class DbWallet(
    @PrimaryKey val id: String,
    val storeKeyId: String,
    val address: String,
    val derivationPath: String,
    val extendedPublicKey: String,
    val coin: String,
    val name: String,
    val platformType: CoinPlatformType,
    val walletConnectChainType: ChainType? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    indices = [Index(value = ["hash"], unique = true)],
)
data class DbStoredKey(
    @PrimaryKey val id: String,
    val hash: String,
    val source: WalletSource,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val data: ByteArray,
    val createdAt: Long,
    val updatedAt: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DbStoredKey

        if (id != other.id) return false
        if (hash != other.hash) return false
        if (source != other.source) return false
        if (!data.contentEquals(other.data)) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + hash.hashCode()
        result = 31 * result + source.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}

data class DbStoreKeyAndWallet(
    @Embedded
    val storedKey: DbStoredKey,
    @Relation(
        parentColumn = "id",
        entityColumn = "storeKeyId",
    )
    val items: List<DbWallet>,
)

data class DbWalletWithStoreKey(
    @Embedded
    val wallet: DbWallet,
    @Relation(
        parentColumn = "storeKeyId",
        entityColumn = "id",
    )
    val storedKey: DbStoredKey,
)