package plus.adaptive.sdk.ui.components.vm

import plus.adaptive.sdk.ui.components.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.APComponentLifecycleListener


internal class APTextComponentViewModel(
    containerViewModel: APComponentContainerViewModel,
    lifecycleListener: APComponentLifecycleListener
) : APBaseComponentViewModel(containerViewModel, lifecycleListener) {

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