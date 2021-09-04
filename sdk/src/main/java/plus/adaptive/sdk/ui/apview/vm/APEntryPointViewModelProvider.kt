package plus.adaptive.sdk.ui.apview.vm

import plus.adaptive.sdk.data.models.APEntryPoint
import plus.adaptive.sdk.data.models.story.Campaign
import plus.adaptive.sdk.ui.apview.newVm.CampaignViewModel


internal interface APEntryPointViewModelProvider {
    /**
     * Get entry point view model by id
     */
    fun getAPEntryPointViewModel(entryPoint: APEntryPoint) : APEntryPointViewModel?
    fun getStoriesViewModel(campaign: Campaign) : CampaignViewModel?
}