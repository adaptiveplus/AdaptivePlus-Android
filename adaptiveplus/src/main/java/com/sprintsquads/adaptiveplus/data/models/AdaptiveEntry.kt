package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class AdaptiveEntry(
    val options: Options,
    val layers: List<AdaptiveLayer>,
    val actions: List<AdaptiveAction>
) : Serializable {

    data class Options(
        val id: String,
        val campaignId: String,
        val showOnce: Boolean
    ) : Serializable
}
