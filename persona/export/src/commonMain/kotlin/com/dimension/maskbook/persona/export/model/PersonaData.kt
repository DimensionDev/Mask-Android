package com.dimension.maskbook.wallet.repository

data class PersonaData(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
)
