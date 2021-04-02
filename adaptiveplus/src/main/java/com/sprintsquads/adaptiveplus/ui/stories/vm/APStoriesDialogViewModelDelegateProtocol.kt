package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.LiveData
import com.sprintsquads.adaptiveplus.data.models.APAction


internal interface APStoriesDialogViewModelDelegateProtocol {
    /**
     * Method to run/execute adaptive plus actions
     *
     * @param actions - list of adaptive plus actions to execute
     * @param campaignId - id of adaptive plus campaign
     * @see APAction
     */
    fun runActions(actions: List<APAction>, campaignId: String)

    /**
     * Getter of stories paused state live data
     *
     * @return is ap stories paused boolean live data
     */
    fun isAPStoriesPausedLiveData() : LiveData<Boolean>
}