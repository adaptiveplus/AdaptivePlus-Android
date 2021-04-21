package plus.adaptive.sdk.data.models.network

import java.io.Serializable


internal data class BaseResponseBody<T>(
    val code: Int,
    val data: T,
    val message: String?
) : Serializable