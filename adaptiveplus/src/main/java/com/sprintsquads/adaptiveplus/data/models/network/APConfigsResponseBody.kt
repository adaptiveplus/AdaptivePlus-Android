package com.sprintsquads.adaptiveplus.data.models.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APConfigsResponseBody(
    @SerializedName("cacheTTL")
    val cacheTTL: Double?,
    @SerializedName("eventsSubmitCount")
    val eventsSubmitCount: Int?,
    @SerializedName("eventsSubmitPeriod")
    val eventsSubmitPeriod: Double?,
    @SerializedName("requestTimeout")
    val requestTimeout: Double?,
    @SerializedName("imageRequestTimeout")
    val imageRequestTimeout: Double?,
    @SerializedName("limitEventTracking")
    val isEventTrackingDisabled: Boolean?
) : Serializable