package plus.adaptive.sdk.ui.components.text

import plus.adaptive.sdk.ui.components.core.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.core.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.core.vm.APBaseComponentViewModel


internal class APTextComponentViewModel(
    containerViewModel: APComponentContainerViewModel,
    lifecycleListener: APComponentLifecycleListener
) : APBaseComponentViewModel(containerViewModel, lifecycleListener) {

    override fun prepare() {
        lifecycleListener.onReady(false)
        mComponentViewController?.prepare()
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