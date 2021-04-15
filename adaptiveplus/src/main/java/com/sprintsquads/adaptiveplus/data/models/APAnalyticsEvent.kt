package com.sprintsquads.adaptiveplus.data.models

import java.io.Serializable


internal data class APAnalyticsEvent(
    val name: String,
    val apViewId: String? = null,
    val campaignId: String? = null,
    val params: Map<String, Any> = mapOf(),
    var createdAt: String? = null
) : Serializable