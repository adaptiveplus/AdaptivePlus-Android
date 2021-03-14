package com.sprintsquads.adaptiveplus.core.managers

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sprintsquads.adaptiveplus.data.models.AdaptiveAction
import com.sprintsquads.adaptiveplus.sdk.data.AdaptiveCustomAction
import com.sprintsquads.adaptiveplus.ui.tag.vm.AdaptiveTagViewModelDelegate


internal class AdaptiveActionsManagerImpl(
    private val fragmentActivity: FragmentActivity,
    private val fragmentManager: FragmentManager,
    private val tagViewModelDelegate: AdaptiveTagViewModelDelegate
) : AdaptiveActionsManager {

    override fun setAdaptiveCustomActionCallback(callback: AdaptiveCustomAction) {
        // TODO("Not yet implemented")
    }

    override fun runAction(action: AdaptiveAction, campaignId: String) {
        // TODO("Not yet implemented")
    }
}