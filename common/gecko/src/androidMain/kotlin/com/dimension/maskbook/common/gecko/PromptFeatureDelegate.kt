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
package com.dimension.maskbook.common.gecko

import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.feature.prompts.PromptFeature

class PromptFeatureDelegate(
    private val fragmentActivity: FragmentActivity,
    store: BrowserStore,
) {

    private val promptFeature = PromptFeature(
        activity = fragmentActivity,
        fragmentManager = fragmentActivity.supportFragmentManager,
        store = store,
        onNeedToRequestPermissions = { permissions ->
            permissionsRequest.launch(permissions)
        }
    )

    private val permissionsRequest: ActivityResultLauncher<Array<String>> by lazy {
        fragmentActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            promptFeature.onPermissionsResult(
                it.keys.toTypedArray(),
                it.values.map { if (it) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED }
                    .toIntArray()
            )
        }
    }

    fun start() = promptFeature.start()
    fun stop() = promptFeature.stop()
    fun onBackPressed() = promptFeature.onBackPressed()

    fun onActivityResult(requestCode: Int, data: Intent?, resultCode: Int) {
        promptFeature.onActivityResult(requestCode, data, resultCode)
    }
}
