package plus.adaptive.sdk.ui.splashscreen.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.core.managers.APSharedPreferences.Companion.CAMPAIGN_WATCHED_COUNT
import plus.adaptive.sdk.core.providers.provideAPPollRepository
import plus.adaptive.sdk.data.models.APSplashScreen
import plus.adaptive.sdk.data.models.components.APBackgroundComponent
import plus.adaptive.sdk.data.models.components.APGIFComponent
import plus.adaptive.sdk.data.models.components.APImageComponent
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.data.models.components.APTextComponent
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.ui.components.core.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.core.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.background.APBackgroundComponentViewModel
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModelProvider
import plus.adaptive.sdk.ui.components.gif.APGIFComponentViewModel
import plus.adaptive.sdk.ui.components.image.APImageComponentViewModel
import plus.adaptive.sdk.ui.components.poll.APPollComponentViewModel
import plus.adaptive.sdk.ui.components.text.APTextComponentViewModel


internal class APSplashScreenDialogViewModel(
    private val splashScreen: APSplashScreen,
    private val preferences: APSharedPreferences?,
    private val userRepository: APUserRepository?
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
                override fun onError() { onComponentError(index) }
                override fun onPreparationProgressUpdate(progress: Float) { }
            }

            when (apLayer.component) {
                is APBackgroundComponent -> APBackgroundComponentViewModel(this, componentLifecycleListener)
                is APImageComponent -> APImageComponentViewModel(this, componentLifecycleListener)
                is APTextComponent -> APTextComponentViewModel(this, componentLifecycleListener)
                is APGIFComponent -> APGIFComponentViewModel(this, componentLifecycleListener)
                is APPollComponent -> APPollComponentViewModel(
                    this, componentLifecycleListener, apLayer.component,
                    provideAPPollRepository(), userRepository, preferences)
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

    private fun onComponentError(index: Int) {
        val viewModel = componentViewModelList[index]

        if (viewModel !is APImageComponentViewModel &&
            viewModel !is APGIFComponentViewModel
        ) {
            onComponentReady(index, true)
        }
    }

    fun increaseSplashScreenWatchedCount() {
        userRepository?.getAPUserId()?.let { userId ->
            val prefKey = "${userId}_${splashScreen.campaignId}_${CAMPAIGN_WATCHED_COUNT}"
            val watchedCount = maxOf(0, preferences?.getInt(prefKey) ?: 0)
            preferences?.saveInt(prefKey, watchedCount + 1)
        }
    }

    fun getLang(): String?{
        return userRepository?.getAPUser()?.device?.locale
    }

}