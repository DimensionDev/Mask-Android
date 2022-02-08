package com.dimension.maskbook.setting.export.model

data class BackupMeta(
    val account: String,
    val personas: Int,
    val associatedAccount: Int,
    val encryptedPost: Int,
    val contacts: Int,
    val file: Int,
    val wallet: Int,
    val json: String,
)
