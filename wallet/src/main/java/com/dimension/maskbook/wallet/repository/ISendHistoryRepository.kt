package com.dimension.maskbook.wallet.repository

import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.DbSendHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.util.*

interface ISendHistoryRepository {
    val recent: Flow<List<SearchAddressData>>
    fun addOrUpdate(address: String)
    fun getByAddress(address: String): Flow<SearchAddressData?>
    fun getOrCreateByAddress(address: String): Flow<SearchAddressData>
}

class SendHistoryRepository(
    private val database: AppDatabase,
) : ISendHistoryRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    override val recent: Flow<List<SearchAddressData>>
        get() = database.sendHistoryDao().getAll().map { it.map { SearchAddressData.fromDb(it) } }

    override fun addOrUpdate(address: String) {
        scope.launch {
            val item = database.sendHistoryDao()
                .getByAddress(address)?.history?.copy(lastSend = System.currentTimeMillis())
                ?: DbSendHistory(
                    UUID.randomUUID().toString(),
                    address,
                    System.currentTimeMillis(),
                    null
                )
            database.sendHistoryDao().add(listOf(item))
        }
    }

    override fun getByAddress(address: String): Flow<SearchAddressData?> {
        return database.sendHistoryDao().getByAddressFlow(address).map {
            if (it != null) {
                SearchAddressData.fromDb(it)
            } else {
                null
            }
        }
    }

    override fun getOrCreateByAddress(address: String): Flow<SearchAddressData> {
        addOrUpdate(address = address)
        return database.sendHistoryDao().getByAddressFlow(address).mapNotNull { it }
            .map { SearchAddressData.fromDb(it) }
    }

}