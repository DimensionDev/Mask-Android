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