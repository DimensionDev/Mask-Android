package com.dimension.maskbook.wallet.viewmodel.settings

import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.Validator
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.BackupRepository
import com.dimension.maskbook.wallet.services.model.DownloadResponse
import com.dimension.maskbook.wallet.viewmodel.register.RemoteBackupRecoveryViewModelBase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EmailBackupViewModel(
    private val backupRepository: BackupRepository,
    private val requestMerge: (target: DownloadResponse, email: String, code: String) -> Unit,
    private val next: (email: String, code: String) -> Unit,
) : RemoteBackupRecoveryViewModelBase(
    {  }
) {
    override fun verifyCode(code: String, value: String): Job = viewModelScope.launch {
        _loading.value = true
        try {
            backupRepository.validateEmailCode(email = value, code = code)
        } catch (e: Throwable) {
            _codeValid.value = false
        }
        try {
            val target = backupRepository.getBackupInformationByEmail(email = value, code = code)
            requestMerge.invoke(target, value, code)
        } catch (e: Throwable) {
            next.invoke(value, code)
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

class PhoneBackupViewModel(
    private val backupRepository: BackupRepository,
    private val requestMerge: (target: DownloadResponse, phone: String, code: String) -> Unit,
    private val next: (phone: String, code: String) -> Unit,
) : RemoteBackupRecoveryViewModelBase(
    {}
) {
    private val _regionCode = MutableStateFlow("+86")
    val regionCode = _regionCode.asStateIn(viewModelScope, "+86")
    fun setRegionCode(value: String) {
        _regionCode.value = value
    }

    override fun verifyCode(code: String, value: String): Job = viewModelScope.launch {
        _loading.value = true
        try {
            backupRepository.validateEmailCode(email = value, code = code)
        } catch (e: Throwable) {
            _codeValid.value = false
        }
        try {
            val target = backupRepository.getBackupInformationByPhone(phone = value, code = code)
            requestMerge.invoke(target, value, code)
        } catch (e: Throwable) {
            next.invoke(value, code)
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