package com.sprintsquads.adaptiveplus.ui.stories.vm

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

    private val componentReadinessList = snap.layers.map { false }.toMutableList()


    override fun getAPComponentViewModel(index: Int): APComponentViewModel? {
        val componentLifecycleListener = object: APComponentLifecycleListener {
            override fun onReady(isReady: Boolean) { onComponentReady(index) }
            override fun onComplete() { onComponentComplete(index) }
            override fun onError() { onComponentError(index) }
            override fun onPreparationProgressUpdate(progress: Float) {
                onComponentPreparationProgressUpdate(index, progress)
            }
        }

        return when (snap.layers.getOrNull(index)?.type) {
            APLayer.Type.BACKGROUND -> APBackgroundComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.IMAGE -> APImageComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.TEXT -> APTextComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.GIF -> APGIFComponentViewModel(this, componentLifecycleListener)
            else -> null
        }
    }

    private fun onComponentReady(index: Int) {
        if (index >= 0 && index < componentReadinessList.size) {
            componentReadinessList[index] = true
            storyViewModelDelegate?.updateSnapReadiness(
                id = snap.id,
                isReady = componentReadinessList.all { it }
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
        // TODO: implement
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