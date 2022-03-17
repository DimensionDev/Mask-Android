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
package com.dimension.maskbook.wallet.usecase.address

import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.SearchAddressData
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

interface GetRecentAddressUseCase {
    operator fun invoke(filter: String = ""): Flow<Result<List<SearchAddressData>>>
}

@Factory(binds = [GetRecentAddressUseCase::class])
class GetRecentAddressUseCaseImpl(
    val repository: ISendHistoryRepository
) : GetRecentAddressUseCase {
    private val recent = repository.recent

    override fun invoke(filter: String): Flow<Result<List<SearchAddressData>>> {
        return recent.map { data ->
            Result.Success(
                if (filter.isEmpty()) {
                    data
                } else {
                    data.filter {
                        it.name?.contains(filter, ignoreCase = true) == true ||
                            it.address.contains(filter, ignoreCase = true)
                    }
                }
            )
        }
    }
}
