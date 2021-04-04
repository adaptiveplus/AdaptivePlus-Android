package com.sprintsquads.adaptiveplus.data.models

import java.io.Serializable


internal data class APError(
    val code: Int,
    val message: String?
) : Serializable
