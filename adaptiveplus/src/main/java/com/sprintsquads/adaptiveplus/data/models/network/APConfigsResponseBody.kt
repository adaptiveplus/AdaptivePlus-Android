package com.sprintsquads.adaptiveplus.data.models.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APConfigsResponseBody(
    @SerializedName("cacheTTL")
    val cacheTTL: Long?,
    @SerializedName("eventsSubmitCount")
    val eventsSubmitCount: Int?,
    @SerializedName("eventsSubmitPeriod")
    val eventsSubmitPeriod: Long?,
    @SerializedName("requestTimeout")
    val requestTimeout: Long?,
    @SerializedName("imageRequestTimeout")
    val imageRequestTimeout: Long?,
    @SerializedName("limitEventTracking")
    val isEventTrackingDisabled: Boolean?
) : Serializable