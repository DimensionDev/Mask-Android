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
package com.dimension.maskbook.common.gecko.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.dimension.maskbook.common.gecko.WebContent
import com.dimension.maskbook.common.gecko.WebContentController
import kotlinx.coroutines.Dispatchers

class MainActivity : FragmentActivity() {
    lateinit var controller: WebContentController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = WebContentController(this, lifecycleScope, Dispatchers.IO).apply {
            installExtensions(
                "borderify@mozilla.org",
                "resource://android/assets/extensions/borderify/"
            )
        }
        setContent {
            MaterialTheme {
                SampleApp(
                    controller
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // controller.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.close()
    }
}

@Composable
fun SampleApp(
    controller: WebContentController
) {
    val canGoBack by controller.canGoBack.collectAsState(initial = false)
    // val canGoForward by controller.canGoForward.collectAsState(initial = false)
    val count by controller.tabCount.collectAsState(initial = 0)
    var url by remember {
        mutableStateOf("")
    }
    Column {
        Row {
            IconButton(
                onClick = { controller.goBack() },
                enabled = canGoBack,
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
            IconButton(
                onClick = { controller.newTab() },
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
            TextButton(
                onClick = {
                }
            ) {
                Text(count.toString())
            }
            IconButton(
                onClick = {
                    controller.loadUrl(url)
                },
            ) {
                Icon(Icons.Filled.Send, contentDescription = null)
            }
            OutlinedTextField(
                value = url,
                onValueChange = {
                    url = it
                }
            )
        }
        WebContent(
            modifier = Modifier.weight(1f),
            controller = controller,
        )
    }
}
