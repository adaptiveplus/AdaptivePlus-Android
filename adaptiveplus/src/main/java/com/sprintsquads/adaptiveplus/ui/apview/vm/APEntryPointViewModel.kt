package com.sprintsquads.adaptiveplus.ui.apview.vm

import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APEntryPoint
import com.sprintsquads.adaptiveplus.data.models.APLayer
import com.sprintsquads.adaptiveplus.data.repositories.APUserRepository
import com.sprintsquads.adaptiveplus.ui.components.APComponentContainerViewModel
import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener
import com.sprintsquads.adaptiveplus.ui.components.vm.APBackgroundComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModelProvider
import com.sprintsquads.adaptiveplus.ui.components.vm.APGIFComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APImageComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APTextComponentViewModel


internal class APEntryPointViewModel(
    private val entryPoint: APEntryPoint,
    private val apViewModelDelegate: APViewModelDelegateProtocol,
    private val preferences: APSharedPreferences,
    private val userRepository: APUserRepository
) : APComponentViewModelProvider, APComponentContainerViewModel {

    private val componentViewModelList: List<APComponentViewModel?> = entryPoint.layers.mapIndexed { index, apLayer ->
        val componentLifecycleListener = object: APComponentLifecycleListener {
            override fun onReady(isReady: Boolean) { onComponentReady(index) }
            override fun onComplete() { onComponentComplete(index) }
            override fun onError() { onComponentError(index) }
        }

        when (apLayer.type) {
            APLayer.Type.BACKGROUND -> APBackgroundComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.IMAGE -> APImageComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.TEXT -> APTextComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.GIF -> APGIFComponentViewModel(this, componentLifecycleListener)
            else -> null
        }
    }


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
        componentViewModelList.forEach { it?.reset() }
    }

    fun runActions(
        actions: List<APAction>,
        campaignId: String
    ) {
        apViewModelDelegate.runActions(actions, campaignId)
    }

    override fun getAPComponentViewModel(index: Int): APComponentViewModel? {
        return componentViewModelList[index]
    }

    override fun isActive(): Boolean {
        val userId = userRepository.getAPUser().apId ?: ""
        val prefKey = "${userId}_${entryPoint.campaignId}_${APSharedPreferences.IS_CAMPAIGN_WATCHED}"
        return !preferences.getBoolean(prefKey)
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