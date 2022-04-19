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
package com.dimension.maskbook.extension.repository

import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.extension.export.model.Site
import com.dimension.maskbook.extension.ext.site
import com.dimension.maskbook.extension.ext.url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class)
class ExtensionRepository(
    private val appScope: CoroutineScope,
    private val controller: WebContentController,
) {
    private val _currentSite = MutableStateFlow(Site.Twitter)
    val currentSite = _currentSite.asSharedFlow()
    fun setCurrentSite(site: Site) {
        _currentSite.value = site
        appScope.launch {
            // workaround for this case:set current site to Twitter first, then set current site to facebook,
            // then go back to twitter tab, currentSite's value is still facebook, if we set current
            // site to facebook again, _currentSite won't update due to MutableStateFlow won't emit
            // same value twice
            if (controller.url.firstOrNull()?.site != _currentSite.value) {
                controller.loadUrl(_currentSite.value.url)
            }
        }
    }
    val isExtensionConnected = controller.isExtensionConnected
    init {
        controller.installExtensions(
            id = "info@dimension.com",
            url = "resource://android/assets/web_extensions/Maskbook/",
        )
        controller.onNavigate = {
            onNavigate(it)
        }
        appScope.launch {
            _currentSite.collect {
                controller.loadUrl(it.url)
            }
        }
        appScope.launch {
            isExtensionConnected.first { it }
            controller.loadUrl(_currentSite.value.url)
        }
    }

    private fun onNavigate(target: String): Boolean {
        return target.site != null
    }
}
