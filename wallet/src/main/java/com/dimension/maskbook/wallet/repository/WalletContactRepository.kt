package com.dimension.maskbook.wallet.repository

import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.DbSendHistoryWithContact
import com.dimension.maskbook.wallet.db.model.DbWalletContact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

data class SearchAddressData(
    val name: String?,// null when there is no name
    val ens: String?,// null when there is no ens
    val address: String,
    val isContact: Boolean
) {
    companion object {
        fun fromDb(data: DbWalletContact) = with(data) {
            SearchAddressData(
                name = name,
                ens = null,
                address = address,
                isContact = true,
            )
        }
        fun fromDb(data: DbSendHistoryWithContact) = with(data) {
            SearchAddressData(
                name = contact?.name ?: history.name,
                ens = null,
                address = history.address,
                isContact = contact != null,
            )
        }
    }
}

interface IWalletContactRepository {
    val contacts: Flow<List<SearchAddressData>>
    fun addOrUpdate(address: String, name: String)
}

class WalletContactRepository(
    private val database: AppDatabase,
) : IWalletContactRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    override val contacts: Flow<List<SearchAddressData>>
        get() = database.walletContactDao().getAll().map { it.map { SearchAddressData.fromDb(it) } }

    override fun addOrUpdate(address: String, name: String) {
        scope.launch {
            val item = database.walletContactDao().getByAddress(address = address)?.copy(name = name)
                ?: DbWalletContact(UUID.randomUUID().toString(), name, address)
            database.walletContactDao().add(listOf(item))
            database.sendHistoryDao().getByAddress(address)?.history?.copy(contactId = item.id)?.let {
                database.sendHistoryDao().add(listOf(it))
            }
        }
    }
}