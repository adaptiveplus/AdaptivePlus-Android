package com.sprintsquads.adaptiveplus.data.models.network

import com.google.gson.annotations.SerializedName
import com.sprintsquads.adaptiveplus.data.models.APUser
import com.sprintsquads.adaptiveplus.data.models.APLocation
import java.io.Serializable


internal data class TokenRequestBody(
    @SerializedName("ap_id")
    val apUserId: String? = null,
    @SerializedName("external_id")
    val externalUserId: String? = null,
    @SerializedName("userDevice")
    val userDevice: APUser.Device? = null,
    @SerializedName("userProperties")
    val userProperties: Map<String, String>? = null,
    @SerializedName("location")
    val userLocation: APLocation? = null
) : Serializable