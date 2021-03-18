package com.sprintsquads.adaptiveplus.ui.apview.vm

import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModelProvider


internal class APEntryPointViewModel(
    private val apViewModelDelegate: APViewModelDelegate
) : APComponentViewModelProvider {

    fun runActions(
        actions: List<APAction>,
        campaignId: String
    ) {
        apViewModelDelegate.runActions(actions, campaignId)
    }

    override fun getAPComponentViewModel(index: Int): APComponentViewModel? {
        // TODO("Not yet implemented")
        return null
    }
}