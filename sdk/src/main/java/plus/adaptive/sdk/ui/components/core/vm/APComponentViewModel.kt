package plus.adaptive.sdk.ui.components.core.vm


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

    /**
     * Getter of has preparation progress updates
     *
     * @return true if has preparation progress updates, false otherwise
     */
    fun hasPreparationProgressUpdates(): Boolean
}