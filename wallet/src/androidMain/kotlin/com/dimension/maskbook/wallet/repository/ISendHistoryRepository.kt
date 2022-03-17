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

import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.DbSendHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.util.UUID

interface ISendHistoryRepository {
    val recent: Flow<List<SearchAddressData>>
    suspend fun addOrUpdate(address: String, name: String)
    fun getByAddress(address: String): Flow<SearchAddressData?>
    fun getOrCreateByAddress(address: String): Flow<SearchAddressData?>
}

@Single(binds = [ISendHistoryRepository::class])
class SendHistoryRepository(
    private val database: AppDatabase,
) : ISendHistoryRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    override val recent: Flow<List<SearchAddressData>>
        get() = database.sendHistoryDao().getAll().map { list ->
            list.sortedByDescending { it.history.lastSend }
                .map { SearchAddressData.fromDb(it) }
        }

    override suspend fun addOrUpdate(address: String, name: String) {
        withContext(scope.coroutineContext) {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getOrCreateByAddress(address: String): Flow<SearchAddressData?> {
        return flow {
            addOrUpdate(address = address, name = "")
            emit(Unit)
        }.flatMapLatest {
            database.sendHistoryDao().getByAddressFlow(address)
                .map {
                    if (it != null) {
                        SearchAddressData.fromDb(it)
                    } else {
                        null
                    }
                }
        }
    }
}
