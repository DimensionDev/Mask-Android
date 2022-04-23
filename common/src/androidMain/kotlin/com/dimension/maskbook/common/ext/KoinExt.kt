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
package com.dimension.maskbook.common.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import moe.tlaster.precompose.viewmodel.ViewModel
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T> getAll(
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
): Set<T> = remember {
    scope.getAll<T>(T::class).toHashSet()
}

@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T : ViewModel> NavController.getNestedNavigationViewModel(
    route: String,
    qualifier: Qualifier? = null,
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    noinline parameters: ParametersDefinition? = null,
): T {
    return remember(route, qualifier, parameters) {
        val backStackEntry = getBackStackEntry(route)
        val owner = ViewModelOwner.from(backStackEntry, backStackEntry)
        scope.getViewModel(qualifier, { owner }, parameters)
    }
}
