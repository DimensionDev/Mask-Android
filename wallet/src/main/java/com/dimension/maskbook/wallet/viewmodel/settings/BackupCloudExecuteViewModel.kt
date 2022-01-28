/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.viewmodel.settings

import androidx.lifecycle.ViewModel
import com.dimension.maskbook.wallet.repository.BackupRepository
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.services.model.AccountType
import kotlinx.coroutines.flow.firstOrNull

class BackupCloudExecuteViewModel(
    private val settingsRepository: ISettingsRepository,
    private val backupRepository: BackupRepository,
    private val personaRepository: IPersonaRepository,
) : ViewModel() {
    suspend fun execute(
        withWallet: Boolean,
        code: String,
        type: String,
        account: String,
    ): Boolean {
        try {
            val json = settingsRepository.createBackupJson(noWallets = !withWallet)
            val abs = personaRepository.persona.firstOrNull()?.joinToString(",") { it.name } ?: return false
            backupRepository.uploadBackup(
                code = code,
                content = json,
                abstract = abs,
                account = account,
                account_type = when (type) {
                    "email" -> AccountType.email
                    "phone" -> AccountType.phone
                    else -> return false
                }
            )
            return true
        } catch (e: Throwable) {
            return false
        }
    }
}
