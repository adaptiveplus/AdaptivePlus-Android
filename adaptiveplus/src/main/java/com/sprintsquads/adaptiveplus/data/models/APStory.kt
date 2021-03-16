package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APStory(
    val id: String,
    val snaps: List<Snap>
) : Serializable {

    data class Snap(
        val id: String,
        val layers: List<APLayer>,
        val width: Double,
        val height: Double,
        val actionLayer: APLayer,
        val showTime: Double
    ) : Serializable
}
