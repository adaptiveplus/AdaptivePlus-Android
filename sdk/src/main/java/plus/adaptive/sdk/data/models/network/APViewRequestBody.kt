package plus.adaptive.sdk.data.models.network

import java.io.Serializable


internal data class APViewRequestBody(
    val parserVersion: Int,
    val hasDrafts: Boolean
) : Serializable