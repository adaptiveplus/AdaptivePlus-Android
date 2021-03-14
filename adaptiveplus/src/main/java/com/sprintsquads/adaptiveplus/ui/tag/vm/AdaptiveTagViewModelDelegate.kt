package com.sprintsquads.adaptiveplus.ui.tag.vm

import com.sprintsquads.adaptiveplus.data.models.AdaptiveAction


internal interface AdaptiveTagViewModelDelegate {
    /**
     * Function to run/execute adaptive actions
     *
     * @param actions - list of adaptive actions to execute
     */
    fun runActions(actions: List<AdaptiveAction>, campaignId: String)
}