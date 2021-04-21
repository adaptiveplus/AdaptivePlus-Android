package plus.adaptive.sdk.core.managers

import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.APStory
import plus.adaptive.sdk.data.listeners.APCustomActionListener


internal interface APActionsManager {
    fun setAPStories(apStories: List<APStory>?)
    fun setAPCustomActionListener(listener: APCustomActionListener?)
    fun runAction(action: APAction)
}