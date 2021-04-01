package com.sprintsquads.adaptiveplus.ui.apview.vm

import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModelProvider


internal class APEntryPointViewModel(
    private val apViewModelDelegate: APViewModelDelegate
) : APComponentViewModelProvider {

    /**
     * Lifecycle method to prepare entry point
     */
    fun prepare() {
        // TODO: implement
    }

    /**
     * Lifecycle method to resume entry point
     */
    fun resume() {
        // TODO: implement
    }

    /**
     * Lifecycle method to pause entry point
     */
    fun pause() {
        // TODO: implement
    }

    /**
     * Lifecycle method to reset entry point
     */
    fun reset() {
        // TODO: implement
    }

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