package com.dimension.maskbook.wallet.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.BuildConfig
import com.dimension.maskbook.wallet.ext.asStateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

abstract class BaseMnemonicPhraseViewModel : ViewModel() {
    protected val _words = MutableStateFlow(emptyList<String>())
    val words = _words.asStateIn(viewModelScope, emptyList())
    val wordsInRandomOrder = _words.map { if (BuildConfig.DEBUG) it else it.shuffled() }
        .asStateIn(viewModelScope, emptyList())
    protected val _selectedWords = MutableStateFlow(emptyList<String>())
    val selectedWords = _selectedWords.asStateIn(viewModelScope, emptyList())
    val correct = _words
        .combine(_selectedWords) { w, s -> w.toTypedArray().contentEquals(s.toTypedArray()) }
        .asStateIn(viewModelScope, false)

    fun refreshWords() {
        _words.value = generateWords()
    }

    fun selectWord(word: String) {
        _selectedWords.value += word
    }

    fun clearWords() {
        _selectedWords.value = emptyList()
    }

    protected abstract fun generateWords(): List<String>

    abstract fun confirm()

    fun deselectWord(word: String) {
        _selectedWords.value -= word
    }
}