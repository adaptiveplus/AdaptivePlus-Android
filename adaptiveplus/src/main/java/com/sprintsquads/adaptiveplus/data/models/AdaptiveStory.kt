package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class AdaptiveStory(
    val id: String,
    val snaps: List<Snap>
) : Serializable {

    data class Snap(
        val id: String,
        val layers: List<AdaptiveLayer>,
        val width: Double,
        val height: Double,
        val actionLayer: AdaptiveLayer,
        val showTime: Double
    ) : Serializable
}
