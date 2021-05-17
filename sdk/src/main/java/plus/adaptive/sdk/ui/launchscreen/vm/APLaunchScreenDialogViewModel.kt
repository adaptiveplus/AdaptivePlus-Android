package plus.adaptive.sdk.ui.launchscreen.vm

import androidx.lifecycle.ViewModel
import plus.adaptive.sdk.data.models.APLaunchScreen
import plus.adaptive.sdk.data.models.APLayer
import plus.adaptive.sdk.ui.components.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.vm.APBackgroundComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APComponentViewModelProvider
import plus.adaptive.sdk.ui.components.vm.APGIFComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APImageComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APTextComponentViewModel


internal class APLaunchScreenDialogViewModel(
    launchScreen: APLaunchScreen
) : ViewModel(), APComponentViewModelProvider, APComponentContainerViewModel {

    private val componentViewModelList: List<APComponentViewModel?> =
        launchScreen.layers.mapIndexed { _, apLayer ->
            val componentLifecycleListener = object: APComponentLifecycleListener {
                override fun onReady(isReady: Boolean) { }
                override fun onComplete() { }
                override fun onError() { }
                override fun onPreparationProgressUpdate(progress: Float) { }
            }

            when (apLayer.type) {
                APLayer.Type.BACKGROUND -> APBackgroundComponentViewModel(this, componentLifecycleListener)
                APLayer.Type.IMAGE -> APImageComponentViewModel(this, componentLifecycleListener)
                APLayer.Type.TEXT -> APTextComponentViewModel(this, componentLifecycleListener)
                APLayer.Type.GIF -> APGIFComponentViewModel(this, componentLifecycleListener)
                else -> null
            }
        }


    override fun getAPComponentViewModel(index: Int): APComponentViewModel? {
        return componentViewModelList[index]
    }

    override fun isActive(): Boolean = false

    override fun showBorder(): Boolean = false

}