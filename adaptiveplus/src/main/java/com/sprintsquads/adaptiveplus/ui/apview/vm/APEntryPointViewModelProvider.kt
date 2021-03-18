package com.sprintsquads.adaptiveplus.ui.apview.vm


internal interface APEntryPointViewModelProvider {
    /**
     * Get entry point view model by id
     */
    fun getAPEntryPointViewModel(id: String) : APEntryPointViewModel?
}