package com.sprintsquads.adaptiveplus.ui.apview.vm

import com.sprintsquads.adaptiveplus.data.models.APAction


internal interface APViewModelDelegate {
    /**
     * Function to run/execute adaptive actions
     *
     * @param actions - list of adaptive actions to execute
     */
    fun runActions(actions: List<APAction>, campaignId: String)
}