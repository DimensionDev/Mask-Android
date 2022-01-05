package com.dimension.maskbook.wallet.viewmodel.recovery

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.BackupMeta
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import android.provider.MediaStore

class RecoveryLocalViewModel(
    private val repository: ISettingsRepository,
    private val uri: Uri,
    private val contentResolver: ContentResolver,
) : ViewModel() {
    enum class LoadState {
        Loading,
        Failed,
        RequirePassword,
        Success,
    }

    private val _meta = MutableStateFlow<BackupMeta?>(null)
    val meta = _meta.asStateIn(viewModelScope, null)
    private val _loadState = MutableStateFlow(LoadState.Loading)
    val loadState = _loadState.asStateIn(viewModelScope, LoadState.Loading)
    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")
    private val _passwordError = MutableStateFlow(false)
    val passwordError = _passwordError.asStateIn(viewModelScope, false)
    private var json = ""

    init {
        loading()
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    private fun loading() = viewModelScope.launch {
//        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
//        val fileName = contentResolver.query(uri, projection, null, null, null)?.use { metaCursor ->
//            if (metaCursor.moveToFirst()) {
//                metaCursor.getString(0)
//            } else {
//                null
//            }
//        } ?: return@launch
        try {

            contentResolver.openInputStream(uri)?.use {
                json = it.bufferedReader().use { it.readText() }
                _meta.value = repository.provideBackupMetaFromJson(json)
                _loadState.value = LoadState.Success
            } ?: run {
                _loadState.value = LoadState.Failed
            }

//            when {
//                fileName.endsWith(".bin", ignoreCase = true) -> {
//                    _loadState.value = LoadState.RequirePassword
//                }
//                fileName.endsWith(".json", ignoreCase = true) -> {
//                    contentResolver.openInputStream(uri)?.use {
//                        json = it.bufferedReader().use { it.readText() }
//                        _meta.value = repository.provideBackupMetaFromJson(json)
//                        _loadState.value = LoadState.Success
//                    } ?: run {
//                        _loadState.value = LoadState.Failed
//                    }
//                }
//                else -> {
//                    throw UnsupportedOperationException()
//                }
//            }
        } catch (e: Throwable) {
            e.printStackTrace()
            _loadState.value = LoadState.Failed
        }
    }

    fun confirmPassword() = viewModelScope.launch {
        _passwordError.value = false
        try {
            //TODO: decrypt bin file to json
            _loadState.value = LoadState.Loading
        } catch (e: Throwable) {
            _loadState.value = LoadState.RequirePassword
            _passwordError.value = true
        }
    }

    fun restore() = viewModelScope.launch {
        if (json.isNotEmpty()) {
            try {
                repository.restoreBackupFromJson(json)
            } catch (e: Throwable) {

            }
        }
    }


}