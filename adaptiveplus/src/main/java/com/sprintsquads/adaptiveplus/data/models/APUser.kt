package com.sprintsquads.adaptiveplus.data.models

import com.sprintsquads.adaptiveplus.sdk.data.APLocation
import java.io.Serializable


internal data class APUser(
    val apId: String?,
    val externalId: String?,
    val deviceId: String,
    val properties: Map<String, Any?>?,
    val location: APLocation?
) : Serializable
