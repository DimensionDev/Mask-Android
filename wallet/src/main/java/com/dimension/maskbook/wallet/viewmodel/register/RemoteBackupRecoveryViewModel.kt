package com.dimension.maskbook.wallet.viewmodel.register

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.Validator
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.BackupRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PhoneRemoteBackupRecoveryViewModel(
    requestNavigate: (NavigateArgs) -> Unit,
    private val backupRepository: BackupRepository,
) : RemoteBackupRecoveryViewModelBase(
    requestNavigate
) {
    private val _regionCode = MutableStateFlow("+86")
    val regionCode = _regionCode.asStateIn(viewModelScope, "+86")
    fun setRegionCode(value: String) {
        _regionCode.value = value
    }

    override suspend fun downloadBackupInternal(code: String, value: String): String {
        return backupRepository.downloadBackupWithPhone(value, code).toString()
    }

    override suspend fun verifyCodeInternal(value: String, code: String) {
        backupRepository.validatePhoneCode(phone = value, code = code)
    }

    override fun validate(value: String): Boolean {
        return Validator.isPhone(_regionCode.value + value)
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendPhoneCode(value)
    }
}

class EmailRemoteBackupRecoveryViewModel(
    requestNavigate: (NavigateArgs) -> Unit,
    private val backupRepository: BackupRepository,
) : RemoteBackupRecoveryViewModelBase(
    requestNavigate
) {
    override suspend fun downloadBackupInternal(code: String, value: String): String {
        return backupRepository.downloadBackupWithEmail(value, code).toString()
    }

    override suspend fun verifyCodeInternal(value: String, code: String) {
        backupRepository.validateEmailCode(email = value, code = code)
    }

    override fun validate(value: String): Boolean {
        return Validator.isEmail(value)
    }

    override suspend fun sendCodeInternal(value: String) {
        backupRepository.sendEmailCode(value)
    }
}


abstract class RemoteBackupRecoveryViewModelBase(
    private val requestNavigate: (NavigateArgs) -> Unit,
) : ViewModel() {

    enum class NavigateTarget {
        Code,
        NoBackup,
        RestoreBackup,
        Next,
    }

    data class NavigateArgs(
        val value: String,
        val target: NavigateTarget,
    )

    private val _value = MutableStateFlow("")
    val value = _value.asStateIn(viewModelScope, "")
    private val _valueValid = MutableStateFlow(true)
    val valueValid = _valueValid.asStateIn(viewModelScope, true)
    private val _code = MutableStateFlow("")
    val code = _code.asStateIn(viewModelScope, "")
    protected val _codeValid = MutableStateFlow(true)
    val codeValid = _codeValid.asStateIn(viewModelScope, true)
    private val _countdown = MutableStateFlow(60)
    val countdown = _countdown.asStateIn(viewModelScope, 60)
    private val _canSend = MutableStateFlow(true)
    val canSend = _canSend.asStateIn(viewModelScope, true)
    protected val _loading = MutableStateFlow(false)
    val loading = _loading.asStateIn(viewModelScope, false)
    private val _time = object : CountDownTimer(60 * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _countdown.value = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
            _countdown.value = 60
            _canSend.value = true
        }
    }

    fun setValue(value: String) {
        _value.value = value
        _valueValid.value = validate(value)
        _codeValid.value = true
    }

    fun setCode(value: String) {
        _code.value = value
    }

    open fun verifyCode(code: String, value: String) = viewModelScope.launch {
        _loading.value = true
        try {
            verifyCodeInternal(value, code)
        } catch (e: Throwable) {
            _codeValid.value = false
            _loading.value = false
            return@launch
        }
        try {
            val uri = downloadBackupInternal(code, value)
            requestNavigate.invoke(NavigateArgs(uri, NavigateTarget.RestoreBackup))
        } catch (e: Throwable) {
            requestNavigate.invoke(NavigateArgs(value, NavigateTarget.NoBackup))
        }
        _loading.value = false
    }

    abstract suspend fun downloadBackupInternal(code: String, value: String): String

    abstract suspend fun verifyCodeInternal(value: String, code: String)

    fun startCountDown() {
        _canSend.value = false
        _time.start()
    }

    protected abstract fun validate(value: String): Boolean
    fun sendCode(value: String) = viewModelScope.launch {
        _loading.value = true
        try {
            sendCodeInternal(value)
            requestNavigate.invoke(NavigateArgs(value, NavigateTarget.Code))
            startCountDown()
        } catch (e: Throwable) {

        }
        _loading.value = false
    }

    abstract suspend fun sendCodeInternal(value: String)
}
