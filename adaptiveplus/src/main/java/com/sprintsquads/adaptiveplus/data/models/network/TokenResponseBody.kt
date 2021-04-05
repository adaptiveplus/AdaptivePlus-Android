package com.sprintsquads.adaptiveplus.data.models.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class TokenResponseBody(
    @SerializedName("access_token")
    val token: String,
    @SerializedName("access_token_expires_in")
    val tokenExpirationDate: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("refresh_token_expires_in")
    val refreshTokenExpirationDate: String,
    @SerializedName("ap_id")
    val apUserId: String
) : Serializable