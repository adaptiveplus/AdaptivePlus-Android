package com.sprintsquads.adaptiveplus.ui.components.vm

import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener


internal class APImageComponentViewModel(
    lifecycleListener: APComponentLifecycleListener
) : APBaseComponentViewModel(lifecycleListener) {

    override fun prepare() {
        lifecycleListener.onReady(false)
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {}

    fun onImageResourceReady() {
        lifecycleListener.onReady(true)
    }

    fun onImageLoadFailed() {
        lifecycleListener.onError()
    }

}