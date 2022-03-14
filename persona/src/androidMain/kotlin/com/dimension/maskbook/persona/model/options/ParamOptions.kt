package com.dimension.maskbook.persona.model.options

import kotlinx.serialization.Serializable

@Serializable
data class ParamOptions<T>(
    val options: T?,
)
