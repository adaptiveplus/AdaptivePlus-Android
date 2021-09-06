package plus.adaptive.sdk.ui.stories.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.core.providers.provideAPPollRepository
import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.APSnap
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
import plus.adaptive.sdk.ui.stories.actionarea.APActionAreaListener
import plus.adaptive.sdk.ui.stories.data.APSnapEvent
import plus.adaptive.sdk.ui.stories.data.APSnapEventInfo


internal class APSnapViewModel(
    private val snap: APSnap,
    private val storyViewModelDelegate: APStoryViewModelDelegateProtocol?,
    private val preferences: APSharedPreferences?,
    private val userRepository: APUserRepository?
) : ViewModel(), APComponentViewModelProvider, APActionAreaListener, APComponentContainerViewModel {

    val snapLoadingProgressLiveData: LiveData<Float>
        get() = _snapLoadingProgressLiveData
    private val _snapLoadingProgressLiveData = MutableLiveData<Float>().apply { value = 0f }

    val isSnapReadyLiveData: LiveData<Boolean>
        get() = _isSnapReadyLiveData
    private val _isSnapReadyLiveData = MutableLiveData<Boolean>().apply { value = false }

    val isErrorStateLiveData: LiveData<Boolean>
        get() = _isErrorStateLiveData
    private val _isErrorStateLiveData = MutableLiveData<Boolean>().apply { value = false }

    private var hasPreparationProgressComponentCount: Int? = null
    private val componentPreparationProgressList = snap.layers.map { 0f }.toMutableList()
    private val componentReadinessList = snap.layers.map { false }.toMutableList()
    private val componentViewModelList: List<APComponentViewModel?> =
        snap.layers.mapIndexed { index, apLayer ->
            val componentLifecycleListener = object: APComponentLifecycleListener {
                override fun onReady(isReady: Boolean) { onComponentReady(index, isReady) }
                override fun onComplete() { onComponentComplete(index) }
                override fun onError() { onComponentError(index) }
                override fun onPreparationProgressUpdate(progress: Float) {
                    onComponentPreparationProgressUpdate(index, progress)
                }
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

    private fun onComponentReady(index: Int, isReady: Boolean) {
        if (index >= 0 && index < componentReadinessList.size) {
            componentReadinessList[index] = isReady

            if (isReady) {
                onComponentPreparationProgressUpdate(index, 1f)
            }

            val isSnapReady = componentReadinessList.all { it }
            _isSnapReadyLiveData.value = isSnapReady

            storyViewModelDelegate?.updateSnapReadiness(
                id = snap.id,
                isReady = isSnapReady
            )
        }
    }

    private fun onComponentComplete(index: Int) {
        // TODO: implement
    }

    private fun onComponentError(index: Int) {
        val viewModel = componentViewModelList[index]

        if (viewModel is APImageComponentViewModel ||
            viewModel is APGIFComponentViewModel
        ) {
            _isErrorStateLiveData.value = true
        }
        else {
            onComponentReady(index, true)
        }
    }

    private fun onComponentPreparationProgressUpdate(index: Int, progress: Float) {
        if (componentViewModelList[index]?.hasPreparationProgressUpdates() != true) {
            return
        }

        if (hasPreparationProgressComponentCount == null) {
            hasPreparationProgressComponentCount =
                componentViewModelList.count { it?.hasPreparationProgressUpdates() == true }
        }

        hasPreparationProgressComponentCount?.let { count ->
            val oldSnapProgress = _snapLoadingProgressLiveData.value ?: 0f
            val oldComponentProgress = componentPreparationProgressList[index]
            val newSnapProgress = oldSnapProgress + (progress - oldComponentProgress) / count
            componentPreparationProgressList[index] = progress
            _snapLoadingProgressLiveData.value = newSnapProgress
        }
    }

    override fun runActions(actions: List<APAction>) {
        APAnalytics.logEvent(
            APAnalyticsEvent(
                name = "action-snap",
                campaignId = storyViewModelDelegate?.getCampaignId() ?: "",
                apViewId = storyViewModelDelegate?.getAPViewId() ?: "",
                params = mapOf("snapId" to snap.id)
            )
        )

        storyViewModelDelegate?.runActions(actions)
    }

    fun runActionAreaActions() {
        when (val actionArea = snap.actionArea) {
            is APSnap.ButtonActionArea -> {
                runActions(actionArea.actions)
            }
            else -> {}
        }
    }

    fun onSnapEvent(event: APSnapEvent) {
        storyViewModelDelegate?.onSnapEvent(
            APSnapEventInfo(snap.id, event)
        )
    }

    fun prepare() {
        _isErrorStateLiveData.value = false
        componentViewModelList.forEach { it?.prepare() }
    }

    fun getLang(): String?{
        return userRepository?.getAPUser()?.device?.locale
    }

    override fun isActive(): Boolean = true

    override fun showBorder(): Boolean = true
}