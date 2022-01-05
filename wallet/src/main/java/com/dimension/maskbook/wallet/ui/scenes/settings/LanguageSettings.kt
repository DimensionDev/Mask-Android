package com.dimension.maskbook.wallet.ui.scenes.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.Language
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskSelection
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.settings.LanguageSettingsViewModel
import org.koin.androidx.compose.getViewModel

val languageMap = Language.values().map {
    it to it.value
}.toMap()

@Composable
fun LanguageSettings(
    onBack: () -> Unit,
) {
    val viewModel: LanguageSettingsViewModel = getViewModel()
    val language by viewModel.language.observeAsState(initial = Language.auto)
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            languageMap.forEach {
                MaskSelection(
                    selected = it.key == language,
                    onClicked = { viewModel.setLanguage(it.key); onBack.invoke() }) {
                    Text(text = it.value, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}