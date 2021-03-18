package com.sprintsquads.adaptiveplus.ui.components.vm

import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener


internal abstract class APBaseComponentViewModel(
    protected val lifecycleListener: APComponentLifecycleListener
) {
    /**
     * Lifecycle method to prepare component
     */
    abstract fun prepare()

    /**
     * Lifecycle method to resume component
     */
    abstract fun resume()

    /**
     * Lifecycle method to pause component
     */
    abstract fun pause()

    /**
     * Lifecycle method to reset component
     */
    abstract fun reset()
}