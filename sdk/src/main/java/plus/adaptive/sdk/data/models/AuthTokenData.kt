package plus.adaptive.sdk.data.models

import java.io.Serializable


internal data class AuthTokenData(
    val token: String?,
    val isFromCache: Boolean = false
) : Serializable