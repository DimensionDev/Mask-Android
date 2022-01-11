package com.dimension.maskbook.repository

import com.dimension.maskbook.wallet.repository.AppData
import com.dimension.maskbook.wallet.repository.AppKey
import com.dimension.maskbook.wallet.repository.IAppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AppRepository : IAppRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _apps = MutableStateFlow(
        AppKey.values().map { AppData(it, true) }
    )

    override val apps = _apps.asSharedFlow()

    private suspend fun refreshApps() {
        _apps.value = _apps.value.let { apps ->
            apps.map {
                it.copy(enabled = JSMethod.App.isPluginEnabled(it.key.id))
            }
        }
    }

    override fun setEnabled(appKey: AppKey, enabled: Boolean) {
        scope.launch {
            JSMethod.App.setPluginStatus(appKey.id, enabled)
            refreshApps()
        }
    }

    override fun init() {
        scope.launch {
            refreshApps()
        }
    }
}