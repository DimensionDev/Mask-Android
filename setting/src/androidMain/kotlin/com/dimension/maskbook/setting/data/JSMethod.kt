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
package com.dimension.maskbook.setting.data

import com.dimension.maskbook.common.ext.execute
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.setting.export.model.Appearance
import com.dimension.maskbook.setting.export.model.BackupPreview
import com.dimension.maskbook.setting.export.model.DataProvider
import com.dimension.maskbook.setting.export.model.Language

internal class JSMethod(
    private val extensionService: ExtensionServices
) {
    suspend fun getTrendingDataSource(): DataProvider {
        return extensionService.execute<Int>("settings_getTrendingDataSource").let { result ->
            DataProvider.values().firstOrNull { it.value == result } ?: DataProvider.COIN_GECKO
        }
    }

    suspend fun setTrendingDataSource(provider: DataProvider) {
        extensionService.execute<Unit>(
            "settings_setTrendingDataSource",
            "provider" to provider.ordinal
        )
    }

    suspend fun getTheme(): Appearance {
        return extensionService.execute<String>("settings_getTheme")?.let {
            Appearance.valueOf(it)
        } ?: Appearance.default
    }

    suspend fun setTheme(appearance: Appearance) {
        extensionService.execute<Unit>("settings_setTheme", "theme" to appearance.name)
    }

    suspend fun getLanguage(): Language {
        return extensionService.execute<String>("settings_getLanguage")?.let {
            Language.parse(it)
        } ?: Language.auto
    }

    suspend fun setLanguage(language: Language) {
        extensionService.execute<Unit>(
            "settings_setLanguage",
            "language" to language.name
        )
    }

    suspend fun createBackupJson(
        noPosts: Boolean = false,
        noWallets: Boolean = false,
        noPersonas: Boolean = false,
        noProfiles: Boolean = false,
        hasPrivateKeyOnly: Boolean = false,
    ): String {
        return extensionService.execute(
            "settings_createBackupJsonString",
            "noPosts" to noPosts,
            "noWallets" to noWallets,
            "noPersonas" to noPersonas,
            "noProfiles" to noProfiles,
            "hasPrivateKeyOnly" to hasPrivateKeyOnly,
        ) ?: ""
    }

    suspend fun getBackupPreviewInfo(json: String): BackupPreview? {
        return extensionService.execute(
            "settings_getBackupPreviewInfo",
            "backupInfo" to json
        )
    }

    suspend fun restoreBackup(json: String) {
        extensionService.execute<Unit>("settings_restoreBackup", "backupInfo" to json)
    }
}
