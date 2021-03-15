package com.sprintsquads.adaptiveplus.data.models

import java.io.Serializable


internal data class AdaptiveColor(
    val startColor: String,
    val endColor: String?,
    val angle: Double?
) : Serializable