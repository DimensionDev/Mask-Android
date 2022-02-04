package com.dimension.maskbook.setting

import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.wallet.repository.Appearance
import kotlinx.coroutines.flow.Flow

class SettingServicesImpl: SettingServices {
    override val appearance: Flow<Appearance>
        get() = TODO("Not yet implemented")
}