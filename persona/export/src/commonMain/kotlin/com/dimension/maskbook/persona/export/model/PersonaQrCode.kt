package com.dimension.maskbook.persona.export.model


data class PersonaQrCode(
    val nickName: String,
    val identifier: String,
    val privateKeyBase64: String,
    val identityWords: String,
    val avatar: String?,
)
