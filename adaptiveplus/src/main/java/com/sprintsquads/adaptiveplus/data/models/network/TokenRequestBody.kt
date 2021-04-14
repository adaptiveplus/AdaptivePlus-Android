package com.sprintsquads.adaptiveplus.data.models.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class TokenRequestBody(
    @SerializedName("ap_id")
    val apUserId: String? = null,
    @SerializedName("external_id")
    val externalUserId: String? = null,
    @SerializedName("userDevice")
    val userDevice: UserDevice,
    @SerializedName("userProperties")
    val userProperties: Map<String, Any?>? = null
) : Serializable {

    data class UserDevice(
        val deviceId: String
    ) : Serializable
}