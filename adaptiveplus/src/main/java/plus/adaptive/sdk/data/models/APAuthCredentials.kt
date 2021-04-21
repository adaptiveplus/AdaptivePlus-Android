package plus.adaptive.sdk.data.models

import java.io.Serializable


internal data class APAuthCredentials(
    val clientId: String,
    val clientSecret: String,
    val grantType: String,
    val channelSecret: String
) : Serializable
