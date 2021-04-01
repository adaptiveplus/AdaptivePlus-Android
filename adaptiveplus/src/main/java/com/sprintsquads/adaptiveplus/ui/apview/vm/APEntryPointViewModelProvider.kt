package com.sprintsquads.adaptiveplus.ui.apview.vm

import com.sprintsquads.adaptiveplus.data.models.APEntryPoint


internal interface APEntryPointViewModelProvider {
    /**
     * Get entry point view model by id
     */
    fun getAPEntryPointViewModel(entryPoint: APEntryPoint) : APEntryPointViewModel?
}