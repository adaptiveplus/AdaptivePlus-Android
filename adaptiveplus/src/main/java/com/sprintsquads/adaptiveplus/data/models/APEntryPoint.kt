package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import com.sprintsquads.adaptiveplus.data.models.actions.APAction
import java.io.Serializable

@Keep
internal data class APEntryPoint(
    val id: String,
    val updatedAt: String,
    val campaignId: String,
    val showOnce: Boolean,
    val layers: List<APLayer>,
    val actions: List<APAction>
) : Serializable
