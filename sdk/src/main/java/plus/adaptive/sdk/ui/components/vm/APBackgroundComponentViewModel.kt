package plus.adaptive.sdk.ui.components.vm

import plus.adaptive.sdk.ui.components.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.APComponentLifecycleListener


internal class APBackgroundComponentViewModel(
    containerViewModel: APComponentContainerViewModel,
    lifecycleListener: APComponentLifecycleListener
) : APBaseComponentViewModel(containerViewModel, lifecycleListener) {

    override fun prepare() {
        lifecycleListener.onReady(true)
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {}
}