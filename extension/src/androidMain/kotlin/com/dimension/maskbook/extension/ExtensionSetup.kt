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
package com.dimension.maskbook.extension

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.gecko.WebContentController
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.repository.ExtensionRepository
import com.dimension.maskbook.extension.ui.generatedRoute
import org.koin.dsl.module

object ExtensionSetup : ModuleSetup {
    override fun NavGraphBuilder.route(navController: NavController, onFinish: () -> Unit) {
        generatedRoute(navController, onFinish)
    }

    override fun dependencyInject() = module {
        scope<FragmentActivity> {
            scoped { WebContentController(get()) }
        }
        single { ExtensionRepository(get()) }
        single<ExtensionServices> { ExtensionServicesImpl(get()) }
    }

    override fun onExtensionReady() {
    }
}
