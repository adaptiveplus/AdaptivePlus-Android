package com.sprintsquads.adaptiveplus.ui.components.vm

import com.sprintsquads.adaptiveplus.ui.components.APComponentContainerViewModel
import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener


internal class APGIFComponentViewModel(
    containerViewModel: APComponentContainerViewModel,
    lifecycleListener: APComponentLifecycleListener
) : APBaseComponentViewModel(containerViewModel, lifecycleListener) {

    override fun prepare() {
        lifecycleListener.onReady(false)
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {
        mComponentViewController?.reset()
    }

    fun onImageResourceReady() {
        lifecycleListener.onReady(true)
    }

    fun onImageLoadFailed() {
        lifecycleListener.onError()
    }

    fun isActive() : Boolean = containerViewModel.isActive()

    fun showBorder() : Boolean = containerViewModel.showBorder()

}