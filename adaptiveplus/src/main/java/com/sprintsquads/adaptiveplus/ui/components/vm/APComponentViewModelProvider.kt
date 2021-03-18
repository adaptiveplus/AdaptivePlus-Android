package com.sprintsquads.adaptiveplus.ui.components.vm


internal interface APComponentViewModelProvider {
    /**
     * Get component view model by layer index
     */
    fun getAPComponentViewModel(index: Int) : APBaseComponentViewModel?
}