package plus.adaptive.sdk.ui.apview.vm

import androidx.lifecycle.LiveData
import plus.adaptive.sdk.data.models.actions.APAction


internal interface APViewModelDelegateProtocol {
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
     * Pause stories progress
     */
    fun pauseAPStories()

    /**
     * Resume stories progress
     */
    fun resumeAPStories()

    /**
     * Method to notify apView that stories dialog has been finished
     *
     * @param campaignId - campaignId of the last shown story or null if undefined
     */
    fun onAPStoriesFinished(campaignId: String?)

    /**
     * Getter of entry point auto scroll period
     *
     * @return autoScroll period in milliseconds
     */
    fun getAutoScrollPeriod() : Long?

    /**
     * Getter of flag to show/hide border
     *
     * @return true/false to show/hide border on components
     */
    fun showBorder() : Boolean

    /**
     * Getter of APView id
     *
     * @return id of APView
     */
    fun getAPViewId() : String
}