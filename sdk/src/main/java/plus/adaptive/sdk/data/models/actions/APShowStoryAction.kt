package plus.adaptive.sdk.data.models.actions

import androidx.annotation.Keep
import plus.adaptive.sdk.data.models.APStory
import java.io.Serializable

@Keep
internal data class APShowStoryAction(
    val story: APStory
) : APAction, Serializable