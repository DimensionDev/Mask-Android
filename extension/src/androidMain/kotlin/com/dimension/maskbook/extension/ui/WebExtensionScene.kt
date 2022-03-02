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

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.gecko.WebContent
import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.extension.export.model.Site
import com.dimension.maskbook.extension.ext.site
import com.dimension.maskbook.localization.R
import org.koin.androidx.compose.get

@NavGraphDestination(
    route = CommonRoute.WebContent,
    deeplink = [Deeplinks.Extension.Extension],
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable
)
@Composable
fun WebExtensionScene(
    navController: NavController,
) {
    val controller = get<WebContentController>()
    MaskScene {
        MaskScaffold(
            topBar = {
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
                                navController.navigate(Uri.parse(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona)))
                            }
                        ) {
                            Image(rememberImagePainter(R.drawable.mask), contentDescription = null)
                        }
                    }
                )
            },
        ) {
            WebContent(
                modifier = Modifier.fillMaxSize(),
                controller = controller
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
