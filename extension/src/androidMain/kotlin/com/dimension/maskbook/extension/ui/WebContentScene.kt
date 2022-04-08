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
package com.dimension.maskbook.extension.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.gecko.WebContent
import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.common.gecko.WebContentViewController
import com.dimension.maskbook.common.ui.widget.AppBarHeight
import com.dimension.maskbook.common.ui.widget.HorizontalScenePadding
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.common.ui.widget.button.clickable
import com.dimension.maskbook.extension.export.model.Site
import com.dimension.maskbook.extension.ext.site
import com.dimension.maskbook.localization.R
import moe.tlaster.koin.compose.get
import moe.tlaster.precompose.navigation.BackHandler
import kotlin.math.roundToInt

@Composable
fun WebContentScene(
    onPersonaClicked: () -> Unit,
    enabledBack: Boolean = true,
    site: Site?,
) {
    val controller = get<WebContentController>()
    val canGoBack by controller.canGoBack.collectAsState(initial = false)
    val state = rememberNestedScrollViewState()
    val appbarHeight = with(LocalDensity.current) {
        AppBarHeight.toPx()
    }
    val viewController = remember {
        WebContentViewController(appbarHeight.roundToInt())
    }
    LaunchedEffect(state.offset) {
        viewController.setVerticalClipping(state.offset.roundToInt())
    }
    BackHandler(
        enabled = enabledBack && canGoBack
    ) {
        controller.goBack()
    }
    MaskScene {
        MaskScaffold {
            NestedScrollView(
                state = state,
                header = {
                    Column {
                        MaskSingleLineTopAppBar(
                            title = {
                                val url by controller.url.collectAsState(initial = "")
                                val title = remember(url) {
                                    getTitleFromUrl(url)
                                }
                                Text(stringResource(title))
                            },
                            actions = {
                                MaskIconButton(
                                    onClick = {
                                        onPersonaClicked.invoke()
                                    }
                                ) {
                                    Image(
                                        rememberImagePainter(R.drawable.mask),
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                        site?.let {
                            var showTips by rememberSaveable { mutableStateOf(true) }
                            if (showTips) {
                                PlatformTips(
                                    it,
                                    onClose = {
                                        showTips = false
                                    }
                                )
                            }
                        }
                    }
                },
                content = {
                    WebContent(
                        modifier = Modifier.fillMaxSize(),
                        controller = controller,
                        viewController = viewController,
                    )
                }
            )
        }
    }
}

@Composable
private fun PlatformTips(
    site: Site,
    onClose: () -> Unit,
) {
    val text = remember(site) {
        when (site) {
            Site.Twitter -> R.string.scene_social_login_in_notify_twitter
            Site.Facebook -> R.string.scene_social_login_in_notify_facebook
        }
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFE5F2FF),
        contentColor = Color(0xFF1D9BF0)
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = HorizontalScenePadding,
                    vertical = 8.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(text),
                color = LocalContentColor.current,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                painter = painterResource(R.drawable.ic_close_square),
                modifier = Modifier.size(24.dp).clickable { onClose.invoke() },
                contentDescription = null,
            )
        }
    }
}

private fun getTitleFromUrl(url: String): Int {
    return when (url.site) {
        Site.Twitter -> R.string.scene_persona_social_twitter
        Site.Facebook -> R.string.scene_persona_social_facebook
        null -> R.string.common_loading
    }
}
