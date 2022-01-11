package com.dimension.maskbook.wallet.ui.scenes.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.Appearance
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskSelection
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.settings.AppearanceSettingsViewModel
import org.koin.androidx.compose.getViewModel

val appearanceMap = mapOf(
    Appearance.default to androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_detail_automatic),
    Appearance.light to androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_detail_light),
    Appearance.dark to androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_detail_dark),
)
@Composable
fun AppearanceSettings(
    onBack: () -> Unit,
) {
    val viewModel: AppearanceSettingsViewModel = getViewModel()
    val appearance by viewModel.appearance.observeAsState(initial = Appearance.default)
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            appearanceMap.forEach {
                MaskSelection(
                    selected = it.key == appearance,
                    onClicked = { viewModel.setAppearance(it.key); onBack.invoke() }) {
                    Text(text = it.value, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}