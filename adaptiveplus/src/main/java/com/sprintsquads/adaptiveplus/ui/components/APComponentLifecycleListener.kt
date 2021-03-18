package com.sprintsquads.adaptiveplus.ui.components


internal interface APComponentLifecycleListener {
    /**
     * Method called on component readiness state changed
     *
     * @param isReady - component readiness state
     */
    fun onReady(isReady: Boolean)

    /**
     * Method called on component progress complete
     */
    fun onComplete()

    /**
     * Method called if component got into error state
     */
    fun onError()
}