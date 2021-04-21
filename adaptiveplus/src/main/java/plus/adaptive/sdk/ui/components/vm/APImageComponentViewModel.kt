package plus.adaptive.sdk.ui.components.vm

import plus.adaptive.sdk.ui.components.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.APComponentLifecycleListener


internal class APImageComponentViewModel(
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

    override fun hasPreparationProgressUpdates(): Boolean = true

    fun onImageResourceReady() {
        lifecycleListener.onReady(true)
    }

    fun onImageLoadFailed() {
        lifecycleListener.onError()
    }

    fun onImageLoadProgressUpdate(progress: Float) {
        lifecycleListener.onPreparationProgressUpdate(progress)
    }

    fun isActive() : Boolean = containerViewModel.isActive()

    fun showBorder() : Boolean = containerViewModel.showBorder()

}