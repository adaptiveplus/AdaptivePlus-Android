package plus.adaptive.sdk.ui.stories.vm

import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.ui.stories.data.APSnapEventInfo


internal interface APStoryViewModelDelegateProtocol {
    /**
     * Method to update snap readiness
     *
     * @param id - id of the snap
     * @param isReady - is snap ready or not
     */
    fun updateSnapReadiness(id: String, isReady: Boolean)

    /**
     * Method to run/execute adaptive plus actions
     *
     * @param actions - list of adaptive plus actions to execute
     * @see APAction
     */
    fun runActions(actions: List<APAction>)

    /**
     * Method to notify story about snap event happened
     *
     * @param eventInfo - snap event info
     */
    fun onSnapEvent(eventInfo: APSnapEventInfo)

    /**
     * Getter of APView id
     *
     * @return id of APView
     */
    fun getAPViewId() : String

    /**
     * Getter of APCampaign id
     *
     * @return id of APCampaign
     */
    fun getCampaignId() : String
}