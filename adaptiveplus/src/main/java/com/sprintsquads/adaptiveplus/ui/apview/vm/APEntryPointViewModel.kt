package com.sprintsquads.adaptiveplus.ui.apview.vm

import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APEntryPoint
import com.sprintsquads.adaptiveplus.data.models.APLayer
import com.sprintsquads.adaptiveplus.ui.components.APComponentContainerViewModel
import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener
import com.sprintsquads.adaptiveplus.ui.components.vm.APBackgroundComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModelProvider
import com.sprintsquads.adaptiveplus.ui.components.vm.APImageComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APTextComponentViewModel


internal class APEntryPointViewModel(
    private val entryPoint: APEntryPoint,
    private val apViewModelDelegate: APViewModelDelegate
) : APComponentViewModelProvider, APComponentContainerViewModel {

    /**
     * Lifecycle method to prepare entry point
     */
    fun prepare() {
        // TODO: implement
    }

    /**
     * Lifecycle method to resume entry point
     */
    fun resume() {
        // TODO: implement
    }

    /**
     * Lifecycle method to pause entry point
     */
    fun pause() {
        // TODO: implement
    }

    /**
     * Lifecycle method to reset entry point
     */
    fun reset() {
        // TODO: implement
    }

    fun runActions(
        actions: List<APAction>,
        campaignId: String
    ) {
        apViewModelDelegate.runActions(actions, campaignId)
    }

    override fun getAPComponentViewModel(index: Int): APComponentViewModel? {
        val componentLifecycleListener = object: APComponentLifecycleListener {
            override fun onReady(isReady: Boolean) { onComponentReady(index) }
            override fun onComplete() { onComponentComplete(index) }
            override fun onError() { onComponentError(index) }
        }

        return when (entryPoint.layers.getOrNull(index)?.type) {
            APLayer.Type.BACKGROUND -> APBackgroundComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.IMAGE -> APImageComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.TEXT -> APTextComponentViewModel(this, componentLifecycleListener)
            else -> null
        }
    }

    override fun isActive(): Boolean {
        // TODO: implement
        return true
    }

    private fun onComponentReady(index: Int) {
        // TODO: implement
    }

    private fun onComponentComplete(index: Int) {
        // TODO: implement
    }

    private fun onComponentError(index: Int) {
        // TODO: implement
    }
}