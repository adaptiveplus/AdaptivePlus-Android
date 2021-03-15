package com.sprintsquads.adaptiveplus.data.models.components

import com.sprintsquads.adaptiveplus.data.models.AdaptiveColor
import java.io.Serializable


internal data class AdaptiveImageComponent(
    val url: String,
    val border: Border
) : AdaptiveComponent, Serializable {

    data class Border(
        val width: Double,
        val activeColor: AdaptiveColor,
        val padding: Double
    ) : Serializable
}