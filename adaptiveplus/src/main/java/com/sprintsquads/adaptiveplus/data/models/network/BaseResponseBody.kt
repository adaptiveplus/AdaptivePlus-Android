package com.sprintsquads.adaptiveplus.data.models.network

import java.io.Serializable


internal data class BaseResponseBody(
    val code: Int,
    val data: String?,
    val message: String?
) : Serializable