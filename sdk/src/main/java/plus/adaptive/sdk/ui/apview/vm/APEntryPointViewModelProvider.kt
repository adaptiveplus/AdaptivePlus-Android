package plus.adaptive.sdk.ui.apview.vm

import plus.adaptive.sdk.data.models.APEntryPoint


internal interface APEntryPointViewModelProvider {
    /**
     * Get entry point view model by id
     */
    fun getAPEntryPointViewModel(entryPoint: APEntryPoint) : APEntryPointViewModel?
}