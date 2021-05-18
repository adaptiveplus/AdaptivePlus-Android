package plus.adaptive.sdk.ui.splashscreen.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import plus.adaptive.sdk.data.models.APSplashScreen
import plus.adaptive.sdk.data.models.APLayer
import plus.adaptive.sdk.ui.components.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.vm.APBackgroundComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APComponentViewModelProvider
import plus.adaptive.sdk.ui.components.vm.APGIFComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APImageComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APTextComponentViewModel


internal class APSplashScreenDialogViewModel(
    splashScreen: APSplashScreen
) : ViewModel(), APComponentViewModelProvider, APComponentContainerViewModel {

    val isSplashScreenReadyLiveData: LiveData<Boolean>
        get() = _isSplashScreenReadyLiveData
    private val _isSplashScreenReadyLiveData = MutableLiveData<Boolean>().apply { value = false }

    private val componentReadinessList = splashScreen.layers.map { false }.toMutableList()
    private val componentViewModelList: List<APComponentViewModel?> =
        splashScreen.layers.mapIndexed { index, apLayer ->
            val componentLifecycleListener = object: APComponentLifecycleListener {
                override fun onReady(isReady: Boolean) { onComponentReady(index, isReady) }
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

    private fun onComponentReady(index: Int, isReady: Boolean) {
        if (index >= 0 && index < componentReadinessList.size) {
            componentReadinessList[index] = isReady

            val isSplashScreenReady = componentReadinessList.all { it }
            _isSplashScreenReadyLiveData.value = isSplashScreenReady
        }
    }

}