package com.dimension.maskbook.wallet.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

data class AppData(
    val id: String,
    val enabled: Boolean,
)

interface IAppRepository {
    val apps: Flow<List<AppData>>
    fun setEnabled(id: String, enabled: Boolean)
    fun init()
}

class FakeAppRepository : IAppRepository {
    private val _apps = MutableStateFlow(
        listOf(
            AppData("com.maskbook.fileservice", true),
            AppData("co.gitcoin", true),
            AppData("co.dhedge", true),
            AppData("com.maskbook.red_packet", true),
//            AppData("com.maskbook.transak", true),
            AppData("com.maskbook.collectibles", true),
            AppData("org.snapshot", true),
            AppData("com.maskbook.ito", true),
            AppData("com.maskbook.tweet", true),
            AppData("com.maskbook.trader", true),
        )
    )
    override val apps = _apps.asSharedFlow()

    override fun setEnabled(id: String, enabled: Boolean) {
        _apps.value = _apps.value.toMutableList().let {
            val index = it.indexOfFirst { it.id == id }
            if (index != -1) {
                it[index] = it[index].copy(enabled = enabled)
            }
            it
        }
    }

    override fun init() {

    }
}

