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
package com.dimension.maskbook

import android.app.Application
import com.dimension.maskbook.common.CommonSetup
import com.dimension.maskbook.entry.BuildConfig
import com.dimension.maskbook.entry.EntrySetup
import com.dimension.maskbook.extension.ExtensionSetup
import com.dimension.maskbook.labs.LabsSetup
import com.dimension.maskbook.persona.PersonaSetup
import com.dimension.maskbook.setting.SettingSetup
import com.dimension.maskbook.wallet.WalletSetup
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.ksp.generated.module

class MaskApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@MaskApp)
            modules(
                CommonSetup.module,
                CommonSetup.dependencyInject(),
                WalletSetup.module,
                WalletSetup.dependencyInject(),
                SettingSetup.module,
                SettingSetup.dependencyInject(),
                LabsSetup.module,
                LabsSetup.dependencyInject(),
                PersonaSetup.module,
                PersonaSetup.dependencyInject(),
                EntrySetup.module,
                EntrySetup.dependencyInject(),
                ExtensionSetup.module,
                ExtensionSetup.dependencyInject(),
            )
        }
    }
}
