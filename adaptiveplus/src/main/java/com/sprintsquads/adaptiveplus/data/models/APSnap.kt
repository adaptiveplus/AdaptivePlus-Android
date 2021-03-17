package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APSnap(
    val id: String,
    val layers: List<APLayer>,
    val width: Double,
    val height: Double,
    val actionLayer: APLayer,
    val showTime: Double
) : Serializable