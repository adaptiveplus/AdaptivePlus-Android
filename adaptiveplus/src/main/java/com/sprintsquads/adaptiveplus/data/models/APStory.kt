package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APStory(
    val id: String,
    var campaignId: String? = null,
    val snaps: List<APSnap>
) : Serializable
