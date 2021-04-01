package com.sprintsquads.adaptiveplus.ui.components.vm


internal interface APComponentViewModel {
    /**
     * Lifecycle method to prepare component
     */
    fun prepare()

    /**
     * Lifecycle method to resume component
     */
    fun resume()

    /**
     * Lifecycle method to pause component
     */
    fun pause()

    /**
     * Lifecycle method to reset component
     */
    fun reset()
}