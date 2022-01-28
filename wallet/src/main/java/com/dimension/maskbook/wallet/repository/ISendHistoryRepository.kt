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
    fun addOrUpdate(address: String, name: String)
    fun getByAddress(address: String): Flow<SearchAddressData?>
    fun getOrCreateByAddress(address: String): Flow<SearchAddressData>
}

class SendHistoryRepository(
    private val database: AppDatabase,
) : ISendHistoryRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    override val recent: Flow<List<SearchAddressData>>
        get() = database.sendHistoryDao().getAll().map { list ->
            list.sortedByDescending { it.history.lastSend }
                .map { SearchAddressData.fromDb(it) }
        }

    override fun addOrUpdate(address: String, name: String) {
        scope.launch {
            with(database.sendHistoryDao()) {
                val currentTime = System.currentTimeMillis()
                if (contains(address) > 0) {
                    if (name.isEmpty()) {
                        updateLastTime(address, currentTime)
                    } else {
                        updateName(address, name, currentTime)
                    }
                } else {
                    val item = DbSendHistory(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        address = address,
                        lastSend = currentTime,
                        contactId = null,
                    )
                    database.sendHistoryDao().add(listOf(item))
                }
            }
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
        addOrUpdate(address = address, name = "")
        return database.sendHistoryDao().getByAddressFlow(address).mapNotNull { it }
            .map { SearchAddressData.fromDb(it) }
    }
}