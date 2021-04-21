package plus.adaptive.sdk.ui.apview


internal interface APEntryPointLifecycleListener {
    /**
     * Method called on entry point readiness state changed
     *
     * @param isReady - entry point readiness state
     */
    fun onReady(isReady: Boolean)

    /**
     * Method called on entry point progress complete
     */
    fun onComplete()

    /**
     * Method called if entry point got into error state
     */
    fun onError()
}