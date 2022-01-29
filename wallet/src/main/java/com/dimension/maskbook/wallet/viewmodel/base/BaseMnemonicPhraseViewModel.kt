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
        val index = _selectedWords.value.indexOf(word)
        if (index >= 0) {
            _selectedWords.value = _selectedWords.value.subList(0, index)
        }
    }
}
