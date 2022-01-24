package com.dimension.maskbook.wallet.viewmodel.settings

import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.Validator
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.BackupRepository
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.viewmodel.register.RemoteBackupRecoveryViewModelBase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EmailSetupViewModel(
    private val personaRepository: IPersonaRepository,
    private val backupRepository: BackupRepository,
    private val requestNavigate: (NavigateArgs) -> Unit,
) : RemoteBackupRecoveryViewModelBase(
    requestNavigate
) {
    override fun verifyCode(code: String, value: String, skipValidate:Boolean): Job = viewModelScope.launch {
        _loading.value = true
        try {
            backupRepository.validateEmailCode(email = value, code = code)
            personaRepository.saveEmailForCurrentPersona(value)
            requestNavigate.invoke(NavigateArgs(value, NavigateTarget.Next))
        } catch (e: Throwable) {
            _codeValid.value = false
        }
        _loading.value = false
    }

    override suspend fun downloadBackupInternal(code: String, value: String): String {
        throw NotImplementedError()
    }

    override suspend fun verifyCodeInternal(value: String, code: String) {
        throw NotImplementedError()
    }

    override fun validate(value: String): Boolean {
        return Validator.isEmail(value)
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendEmailCode(value)
    }
}

class PhoneSetupViewModel(
    private val personaRepository: IPersonaRepository,
    private val requestNavigate: (NavigateArgs) -> Unit,
    private val backupRepository: BackupRepository,
) : RemoteBackupRecoveryViewModelBase(
    requestNavigate
) {
    private val _regionCode = MutableStateFlow("+86")
    val regionCode = _regionCode.asStateIn(viewModelScope, "+86")
    fun setRegionCode(value: String) {
        _regionCode.value = value
    }

    override fun verifyCode(code: String, value: String, skipValidate:Boolean): Job = viewModelScope.launch {
        _loading.value = true
        try {
            backupRepository.validatePhoneCode(phone = value, code = code)
            personaRepository.savePhoneForCurrentPersona(value)
            requestNavigate.invoke(NavigateArgs(value, NavigateTarget.Next))
        } catch (e: Throwable) {
            _codeValid.value = false
        }
        _loading.value = false
    }

    override suspend fun downloadBackupInternal(code: String, value: String): String {
        throw NotImplementedError()
    }

    override suspend fun verifyCodeInternal(value: String, code: String) {
        throw NotImplementedError()
    }

    override fun validate(value: String): Boolean {
        return Validator.isPhone(_regionCode.value + value)
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendPhoneCode(value)
    }
}