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
package com.dimension.maskbook.setting.services.model

import kotlinx.serialization.Serializable

@Serializable
enum class AccountType {
    email,
    phone,
}

@Serializable
enum class Scenario {
    backup,
    create_binding,
    change_binding
}

@Serializable
enum class Locale {
    en,
    zh
}

@Serializable
data class SendCodeBody(
    val account_type: AccountType,
    val account: String,
    val scenario: Scenario,
    val locale: Locale,
)

@Serializable
data class ValidateCodeBody(
    val code: String,
    val account_type: AccountType,
    val account: String,
)

@Serializable
data class UploadBody(
    val code: String,
    val account_type: AccountType,
    val account: String,
    val abstract: String,
)

@Serializable
data class UploadResponse(
    val upload_url: String?,
)

@Serializable
data class DownloadResponse(
    val download_url: String?,
    val size: Long?,
    val uploaded_at: Long?,
    val abstract: String?,
)
