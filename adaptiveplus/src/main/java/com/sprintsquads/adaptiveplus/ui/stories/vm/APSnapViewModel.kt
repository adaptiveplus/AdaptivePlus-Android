package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprintsquads.adaptiveplus.data.models.actions.APAction
import com.sprintsquads.adaptiveplus.data.models.APLayer
import com.sprintsquads.adaptiveplus.data.models.APSnap
import com.sprintsquads.adaptiveplus.ui.components.APComponentContainerViewModel
import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener
import com.sprintsquads.adaptiveplus.ui.components.vm.APBackgroundComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModelProvider
import com.sprintsquads.adaptiveplus.ui.components.vm.APGIFComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APImageComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APTextComponentViewModel
import com.sprintsquads.adaptiveplus.ui.stories.actionarea.APActionAreaListener
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEvent
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEventInfo


internal class APSnapViewModel(
    private val snap: APSnap,
    private val storyViewModelDelegate: APStoryViewModelDelegateProtocol?
) : ViewModel(), APComponentViewModelProvider, APActionAreaListener, APComponentContainerViewModel {

    val snapLoadingProgressLiveData: LiveData<Float>
        get() = _snapLoadingProgressLiveData
    private val _snapLoadingProgressLiveData = MutableLiveData<Float>().apply { value = 0f }

    val isSnapReadyLiveData: LiveData<Boolean>
        get() = _isSnapReadyLiveData
    private val _isSnapReadyLiveData = MutableLiveData<Boolean>().apply { value = false }

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
        // TODO: implement
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

    override fun isActive(): Boolean = true

    override fun showBorder(): Boolean = true
}