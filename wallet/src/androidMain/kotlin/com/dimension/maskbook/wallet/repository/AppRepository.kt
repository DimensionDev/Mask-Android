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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class AppKey(val id: String) {
    FileService("com.maskbook.fileservice"),
    ITO("com.maskbook.ito"),
    LuckDrop("com.maskbook.red_packet"),
    Transak("com.maskbook.transak"),
    Snapshot("org.snapshot"),
    DHEDGE("co.dhedge"),
    GitCoin("co.gitcoin"),
    Swap("com.maskbook.trader"),
    Collectibles("com.maskbook.collectibles"),
    Valuables("com.maskbook.tweet"),
}

data class AppData(
    val key: AppKey,
    val enabled: Boolean,
)

interface IAppRepository {
    val apps: Flow<List<AppData>>
    fun setEnabled(appKey: AppKey, enabled: Boolean)
    fun init()
}

class FakeAppRepository : IAppRepository {

    private val _apps = MutableStateFlow(
        AppKey.values().map { AppData(it, true) }
    )
    override val apps = _apps.asSharedFlow()

    override fun setEnabled(appKey: AppKey, enabled: Boolean) {
        _apps.value = _apps.value.toMutableList().also { apps ->
            val index = apps.indexOfFirst { it.key == appKey }
            if (index != -1) {
                apps[index] = apps[index].copy(enabled = enabled)
            }
        }
    }

    override fun init() {
    }
}
