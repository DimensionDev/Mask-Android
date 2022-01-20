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