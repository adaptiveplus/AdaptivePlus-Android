package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.ViewModel
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APLayer
import com.sprintsquads.adaptiveplus.data.models.APSnap
import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener
import com.sprintsquads.adaptiveplus.ui.components.vm.APBackgroundComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APBaseComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModelProvider
import com.sprintsquads.adaptiveplus.ui.components.vm.APImageComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APTextComponentViewModel
import com.sprintsquads.adaptiveplus.ui.stories.actionarea.APActionAreaListener
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEvent
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEventInfo


internal class APSnapViewModel(
    private val snap: APSnap,
    private val storyViewModelDelegate: APStoryViewModelDelegate?
) : ViewModel(), APComponentViewModelProvider, APActionAreaListener {

    private val componentReadinessList = snap.layers.map { false }.toMutableList()


    override fun getAPComponentViewModel(index: Int): APBaseComponentViewModel? {
        val componentLifecycleListener = object: APComponentLifecycleListener {
            override fun onReady(isReady: Boolean) { onComponentReady(index) }
            override fun onComplete() { onComponentComplete(index) }
            override fun onError() { onComponentError(index) }
        }

        return when (snap.layers.getOrNull(index)?.type) {
            APLayer.Type.BACKGROUND -> APBackgroundComponentViewModel(componentLifecycleListener)
            APLayer.Type.IMAGE -> APImageComponentViewModel(componentLifecycleListener)
            APLayer.Type.TEXT -> APTextComponentViewModel(componentLifecycleListener)
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
}