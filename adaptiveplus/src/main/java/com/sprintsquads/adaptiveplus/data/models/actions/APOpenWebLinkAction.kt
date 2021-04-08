package com.sprintsquads.adaptiveplus.data.models.actions

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APOpenWebLinkAction(
    val url: String
) : APAction, Serializable