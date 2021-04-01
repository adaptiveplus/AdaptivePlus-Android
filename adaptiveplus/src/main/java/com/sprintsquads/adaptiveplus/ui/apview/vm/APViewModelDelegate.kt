package com.sprintsquads.adaptiveplus.ui.apview.vm

import androidx.lifecycle.LiveData
import com.sprintsquads.adaptiveplus.data.models.APAction


internal interface APViewModelDelegate {
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

    /**
     * Pause stories progress
     */
    fun pauseAPStories()

    /**
     * Resume stories progress
     */
    fun resumeAPStories()

    /**
     * Method to notify apView that stories dialog was dismissed
     */
    fun onAPStoriesDismissed()
}