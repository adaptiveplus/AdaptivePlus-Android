package plus.adaptive.sdk.data.models

import java.io.Serializable


internal data class APError(
    val code: Int,
    val message: String?
) : Serializable
