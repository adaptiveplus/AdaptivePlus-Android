package com.sprintsquads.adaptiveplus.core.managers

import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.sdk.data.APCustomAction


internal interface APActionsManager {
    fun setAPCustomAction(callback: APCustomAction)
    fun runAction(action: APAction, campaignId: String)
}