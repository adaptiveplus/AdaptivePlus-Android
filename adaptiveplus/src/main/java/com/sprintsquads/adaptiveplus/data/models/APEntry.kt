package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APEntry(
    val options: Options,
    val layers: List<APLayer>,
    val actions: List<APAction>
) : Serializable {

    data class Options(
        val id: String,
        val campaignId: String,
        val showOnce: Boolean
    ) : Serializable
}
