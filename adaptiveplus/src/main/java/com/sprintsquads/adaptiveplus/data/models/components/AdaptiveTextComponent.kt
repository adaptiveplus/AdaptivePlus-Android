package com.sprintsquads.adaptiveplus.data.models.components

import com.sprintsquads.adaptiveplus.data.models.AdaptiveFont
import java.io.Serializable


internal data class AdaptiveTextComponent(
    val value: String,
    val font: AdaptiveFont?
) : AdaptiveComponent, Serializable