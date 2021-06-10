package plus.adaptive.sdk.ui.components.core.vm


internal interface APComponentViewModelProvider {
    /**
     * Get component view model by layer index
     */
    fun getAPComponentViewModel(index: Int) : APComponentViewModel?
}