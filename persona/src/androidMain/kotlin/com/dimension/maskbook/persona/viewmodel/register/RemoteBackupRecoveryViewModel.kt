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
package com.dimension.maskbook.persona.viewmodel.register

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.Validator
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.setting.export.BackupServices
import com.dimension.maskbook.setting.export.model.BackupFileMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PhoneRemoteBackupRecoveryViewModel(
    requestNavigate: (NavigateArgs) -> Unit,
    private val backupServices: BackupServices,
) : RemoteBackupRecoveryViewModelBase(
    requestNavigate
) {
    private val _regionCode = MutableStateFlow("+86")
    val regionCode = _regionCode.asStateIn(viewModelScope, "+86")
    fun setRegionCode(value: String) {
        _regionCode.value = value
    }

    override suspend fun downloadBackupInternal(code: String, value: String): BackupFileMeta {
        return backupServices.downloadBackupWithPhone(value, code)
    }

    override suspend fun verifyCodeInternal(value: String, code: String) {
        backupServices.validatePhoneCode(phone = value, code = code)
    }

    override fun validate(value: String): Boolean {
        return Validator.isPhone(_regionCode.value + value)
    }

    override suspend fun sendCodeInternal(value: String) {
        backupServices.sendPhoneCode(value)
    }
}

class EmailRemoteBackupRecoveryViewModel(
    requestNavigate: (NavigateArgs) -> Unit,
    private val backupServices: BackupServices,
) : RemoteBackupRecoveryViewModelBase(
    requestNavigate
) {
    override suspend fun downloadBackupInternal(code: String, value: String): BackupFileMeta {
        return backupServices.downloadBackupWithEmail(value, code)
    }

    override suspend fun verifyCodeInternal(value: String, code: String) {
        backupServices.validateEmailCode(email = value, code = code)
    }

    override fun validate(value: String): Boolean {
        return Validator.isEmail(value)
    }

    override suspend fun sendCodeInternal(value: String) {
        backupServices.sendEmailCode(value)
    }
}

// FIXME: 2022/2/7 Remove this class
abstract class RemoteBackupRecoveryViewModelBase(
    private val requestNavigate: (NavigateArgs) -> Unit,
) : ViewModel() {

    enum class NavigateTarget {
        Code,
        NoBackup,
        RestoreBackup,
        Next,
    }

    sealed interface NavigateArgs {
        val target: NavigateTarget
    }

    data class StringNavigateArgs(
        val value: String,
        override val target: NavigateTarget,
    ) : NavigateArgs

    data class BackupFileMetaNavigateArgs(
        val backupFileMeta: BackupFileMeta,
        override val target: NavigateTarget,
    ) : NavigateArgs

    // data class NavigateArgs(
    //     val value: String,
    //     val target: NavigateTarget,
    // )

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

    // if skipValidate is true then verifyCodeInternal will not be called
    open fun verifyCode(code: String, value: String, skipValidate: Boolean = false) = viewModelScope.launch {
        _loading.value = true
        try {
            if (!skipValidate) verifyCodeInternal(value, code)
        } catch (e: Throwable) {
            _codeValid.value = false
            _loading.value = false
            return@launch
        }
        try {
            val meta = downloadBackupInternal(code, value)
            requestNavigate.invoke(BackupFileMetaNavigateArgs(meta, NavigateTarget.RestoreBackup))
        } catch (e: Throwable) {
            requestNavigate.invoke(StringNavigateArgs(value, NavigateTarget.NoBackup))
        }
        _loading.value = false
    }

    abstract suspend fun downloadBackupInternal(code: String, value: String): BackupFileMeta

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
            requestNavigate.invoke(StringNavigateArgs(value, NavigateTarget.Code))
            startCountDown()
        } catch (e: Throwable) {
        }
        _loading.value = false
    }

    abstract suspend fun sendCodeInternal(value: String)
}
