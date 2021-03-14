package com.sprintsquads.adaptiveplus.core.managers

import com.sprintsquads.adaptiveplus.data.models.AdaptiveAction
import com.sprintsquads.adaptiveplus.sdk.data.AdaptiveCustomAction


internal interface AdaptiveActionsManager {
    fun setAdaptiveCustomActionCallback(callback: AdaptiveCustomAction)
    fun runAction(action: AdaptiveAction, campaignId: String)
}