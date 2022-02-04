package com.dimension.maskbook.wallet.services.model

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
