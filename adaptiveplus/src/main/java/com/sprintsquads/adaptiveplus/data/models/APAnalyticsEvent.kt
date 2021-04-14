package com.sprintsquads.adaptiveplus.data.models

import java.io.Serializable


internal data class APAnalyticsEvent(
    val name: String,
    val campaignId: String,
    val apViewId: String,
    val params: Map<String, Any>,
    var createdAt: String? = null
) : Serializable