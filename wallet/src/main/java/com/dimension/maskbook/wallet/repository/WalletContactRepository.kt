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
import com.dimension.maskbook.wallet.db.model.DbSendHistoryWithContact
import com.dimension.maskbook.wallet.db.model.DbWalletContact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

data class SearchAddressData(
    val name: String?, // null when there is no name
    val ens: String?, // null when there is no ens
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
