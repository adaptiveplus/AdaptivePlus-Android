package plus.adaptive.sdk.data.models.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class TokenResponseBody(
    @SerializedName("access_token")
    val token: String,
    @SerializedName("access_token_expires_in")
    val tokenExpiresIn: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("refresh_token_expires_in")
    val refreshTokenExpiresIn: String,
    @SerializedName("ap_id")
    val apUserId: String
) : Serializable