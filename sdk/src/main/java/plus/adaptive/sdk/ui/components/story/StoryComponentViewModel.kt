package plus.adaptive.sdk.ui.components.story

import plus.adaptive.sdk.ui.components.core.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.core.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.core.vm.APBaseComponentViewModel


internal class StoryComponentViewModel(
    containerViewModel: APComponentContainerViewModel,
    lifecycleListener: APComponentLifecycleListener
) : APBaseComponentViewModel(containerViewModel, lifecycleListener) {

    override fun prepare() {
        lifecycleListener.onReady(false)
        mComponentViewController?.prepare()
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

    fun onTextResourceReady() {
        lifecycleListener.onReady(true)
    }

    fun onError() {
        lifecycleListener.onError()
    }

}