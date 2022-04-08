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
package com.dimension.maskbook.common.viewmodel

import com.dimension.maskbook.common.BuildConfig
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.model.MnemonicWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

abstract class BaseMnemonicPhraseViewModel : ViewModel() {
    protected val _words = MutableStateFlow(emptyList<MnemonicWord>())
    val words = _words.asStateIn(viewModelScope, emptyList())
    val wordsInRandomOrder = _words.map { if (BuildConfig.DEBUG) it else it.shuffled() }
        .asStateIn(viewModelScope, emptyList())
    protected val _selectedWords = MutableStateFlow(emptyList<MnemonicWord>())
    val selectedWords = _selectedWords.asStateIn(viewModelScope, emptyList())
    val correct = _words
        .combine(_selectedWords) { w, s -> w.toTypedArray().contentEquals(s.toTypedArray()) }
        .asStateIn(viewModelScope, false)

    fun refreshWords() {
        _words.value = generateWords().mapIndexed { index, word -> MnemonicWord(index, word) }
    }

    fun selectWord(word: MnemonicWord) {
        _selectedWords.value += word
    }

    fun clearWords() {
        _selectedWords.value = emptyList()
    }

    protected abstract fun generateWords(): List<String>

    abstract fun confirm()

    fun deselectWord(word: MnemonicWord) {
        val index = _selectedWords.value.indexOf(word)
        if (index >= 0) {
            _selectedWords.value = _selectedWords.value.subList(0, index)
        }
    }
}
