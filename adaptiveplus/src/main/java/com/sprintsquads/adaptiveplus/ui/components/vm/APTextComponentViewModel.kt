package com.sprintsquads.adaptiveplus.ui.components.vm

import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener


internal class APTextComponentViewModel(
    lifecycleListener: APComponentLifecycleListener
) : APBaseComponentViewModel(lifecycleListener) {

    override fun prepare() {
        lifecycleListener.onReady(false)
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {}

    fun onTextResourceReady() {
        lifecycleListener.onReady(true)
    }

    fun onError() {
        lifecycleListener.onError()
    }

}