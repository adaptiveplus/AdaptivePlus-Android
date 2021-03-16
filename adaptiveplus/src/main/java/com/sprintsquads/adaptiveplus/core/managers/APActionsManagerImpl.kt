package com.sprintsquads.adaptiveplus.core.managers

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.sdk.data.APCustomAction
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelDelegate


internal class APActionsManagerImpl(
    private val fragmentActivity: FragmentActivity,
    private val fragmentManager: FragmentManager,
    private val apViewModelDelegate: APViewModelDelegate
) : APActionsManager {

    override fun setAPCustomAction(apCustomAction: APCustomAction) {
        // TODO("Not yet implemented")
    }

    override fun runAction(action: APAction, campaignId: String) {
        // TODO("Not yet implemented")
    }
}