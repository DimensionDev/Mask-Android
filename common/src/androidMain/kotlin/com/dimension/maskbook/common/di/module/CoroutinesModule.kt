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
package com.dimension.maskbook.common.di.module

import com.dimension.maskbook.common.di.scope.appScope
import com.dimension.maskbook.common.di.scope.defaultDispatcher
import com.dimension.maskbook.common.di.scope.ioDispatcher
import com.dimension.maskbook.common.di.scope.mainDispatcher
import com.dimension.maskbook.common.di.scope.mainImmediateDispatcher
import com.dimension.maskbook.common.di.scope.preferenceCoroutineContext
import com.dimension.maskbook.common.di.scope.repositoryCoroutineContext
import com.dimension.maskbook.common.util.coroutineExceptionHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module

internal fun Module.coroutinesModule() {
    single(defaultDispatcher) { Dispatchers.Default }
    single(ioDispatcher) { Dispatchers.IO }
    single<CoroutineDispatcher>(mainDispatcher) { Dispatchers.Main }
    single<CoroutineDispatcher>(mainImmediateDispatcher) { Dispatchers.Main.immediate }

    single(preferenceCoroutineContext) {
        NonCancellable + Dispatchers.Default
    }
    single(repositoryCoroutineContext) {
        coroutineExceptionHandler + Dispatchers.Default
    }

    single(appScope) {
        CoroutineScope(
            coroutineExceptionHandler + SupervisorJob()
        )
    }
}
