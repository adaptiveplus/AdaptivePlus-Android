package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class AdaptivePadding(
    val top: Double,
    val bottom: Double,
    val left: Double,
    val right: Double
) : Serializable