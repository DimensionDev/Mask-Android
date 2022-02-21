package com.dimension.maskbook.persona.services


@kotlinx.serialization.Serializable
data class CreateProofParams(
    val action: String, // create or delete
    val platform: String,
    val identity: String,
    val proof_location: String,
    val public_key: String,
    val extra: String? = null
)