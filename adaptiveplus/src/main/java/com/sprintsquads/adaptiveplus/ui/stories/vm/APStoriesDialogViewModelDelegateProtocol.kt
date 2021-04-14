package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.LiveData
import com.sprintsquads.adaptiveplus.data.models.actions.APAction


internal interface APStoriesDialogViewModelDelegateProtocol {
    /**
     * Method to run/execute adaptive plus actions
     *
     * @param actions - list of adaptive plus actions to execute
     * @see APAction
     */
    fun runActions(actions: List<APAction>)

    /**
     * Getter of stories paused state live data
     *
     * @return is ap stories paused boolean live data
     */
    fun isAPStoriesPausedLiveData() : LiveData<Boolean>

    /**
     * Getter of APView id
     *
     * @return id of APView
     */
    fun getAPViewId() : String
}