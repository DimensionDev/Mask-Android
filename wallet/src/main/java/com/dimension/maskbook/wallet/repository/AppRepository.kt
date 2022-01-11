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
