package com.sprintsquads.adaptiveplus.core.managers

import com.sprintsquads.adaptiveplus.data.models.actions.APAction
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.data.listeners.APCustomActionListener


internal interface APActionsManager {
    fun setAPStories(apStories: List<APStory>?)
    fun setAPCustomActionListener(listener: APCustomActionListener?)
    fun runAction(action: APAction)
}