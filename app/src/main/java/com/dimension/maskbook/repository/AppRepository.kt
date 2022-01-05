package com.dimension.maskbook.repository

import com.dimension.maskbook.wallet.repository.AppData
import com.dimension.maskbook.wallet.repository.IAppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AppRepository : IAppRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _apps = MutableStateFlow(
        listOf(
            AppData("com.maskbook.fileservice", true),
            AppData("co.gitcoin", true),
            AppData("co.dhedge", true),
            AppData("com.maskbook.red_packet", true),
            AppData("com.maskbook.transak", true),
            AppData("com.maskbook.collectibles", true),
            AppData("org.snapshot", true),
            AppData("com.maskbook.ito", true),
            AppData("com.maskbook.tweet", true),
            AppData("com.maskbook.trader", true),
        )
    )
    override val apps = _apps.asSharedFlow()

    private suspend fun refreshApps() {
        _apps.value = _apps.value.let {
            it.map {
                it.copy(enabled = JSMethod.App.isPluginEnabled(it.id))
            }
        }
    }

    override fun setEnabled(id: String, enabled: Boolean) {
        scope.launch {
            JSMethod.App.setPluginStatus(id, enabled)
            refreshApps()
        }
    }

    override fun init() {
        scope.launch {
            refreshApps()
        }
    }
}