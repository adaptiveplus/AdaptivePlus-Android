package com.sprintsquads.adaptiveplus.data.models.actions

import androidx.annotation.Keep
import com.sprintsquads.adaptiveplus.data.models.APStory
import java.io.Serializable

@Keep
internal data class APShowStoryAction(
    val story: APStory
) : APAction, Serializable