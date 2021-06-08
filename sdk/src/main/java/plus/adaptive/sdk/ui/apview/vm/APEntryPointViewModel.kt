package plus.adaptive.sdk.ui.apview.vm

import android.os.Handler
import android.os.Looper
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.APEntryPoint
import plus.adaptive.sdk.data.models.APLayer
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.ui.apview.APEntryPointLifecycleListener
import plus.adaptive.sdk.ui.components.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.vm.APBackgroundComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APComponentViewModelProvider
import plus.adaptive.sdk.ui.components.vm.APGIFComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APImageComponentViewModel
import plus.adaptive.sdk.ui.components.vm.APTextComponentViewModel


internal class APEntryPointViewModel(
    private val entryPoint: APEntryPoint,
    private val preferences: APSharedPreferences,
    private val userRepository: APUserRepository,
    private val lifecycleListener: APEntryPointLifecycleListener,
    private val apViewVMDelegate: APViewVMDelegateProtocol
) : APComponentViewModelProvider, APComponentContainerViewModel {

    private val componentViewModelList: List<APComponentViewModel?> = entryPoint.layers.mapIndexed { index, apLayer ->
        val componentLifecycleListener = object: APComponentLifecycleListener {
            override fun onReady(isReady: Boolean) { onComponentReady(index, isReady) }
            override fun onComplete() { onComponentComplete(index) }
            override fun onError() { onComponentError(index) }
            override fun onPreparationProgressUpdate(progress: Float) {}
        }

        when (apLayer.type) {
            APLayer.Type.BACKGROUND -> APBackgroundComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.IMAGE -> APImageComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.TEXT -> APTextComponentViewModel(this, componentLifecycleListener)
            APLayer.Type.GIF -> APGIFComponentViewModel(this, componentLifecycleListener)
            else -> null
        }
    }

    private val componentReadinessSet = mutableSetOf<Int>()
    private var progressHandler: Handler? = null
    private val progressCompleteTask = Runnable {
        pause()
        lifecycleListener.onComplete()
    }
    private var isResumed: Boolean = false
    private var doResumeOnReady: Boolean = false


    /**
     * Lifecycle method to prepare entry point
     */
    fun prepare() {
        lifecycleListener.onReady(false)

        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
        Looper.myLooper()?.let {
            progressHandler = Handler(it)
        }
    }

    /**
     * Lifecycle method to resume entry point
     */
    fun resume() {
        if (!isResumed) {
            if (isReady()) {
                isResumed = true
                componentViewModelList.forEach { it?.resume() }

                apViewVMDelegate.getAutoScrollPeriod()?.let { autoScrollPeriod ->
                    progressHandler?.postDelayed(progressCompleteTask, autoScrollPeriod)
                }

                APAnalytics.logEvent(
                    APAnalyticsEvent(
                        name = "shown-entryPoint",
                        campaignId = entryPoint.campaignId,
                        apViewId = apViewVMDelegate.getAPViewId(),
                        params = mapOf("entryPointId" to entryPoint.id)
                    )
                )
            } else {
                doResumeOnReady = true
            }
        }
    }

    /**
     * Lifecycle method to pause entry point
     */
    fun pause() {
        isResumed = false
        doResumeOnReady = false
        componentViewModelList.forEach { it?.pause() }
        progressHandler?.removeCallbacks(progressCompleteTask)
    }

    /**
     * Lifecycle method to reset entry point
     */
    fun reset() {
        componentViewModelList.forEach { it?.reset() }
    }

    fun runActions(actions: List<APAction>) {
        APAnalytics.logEvent(
            APAnalyticsEvent(
                name = "action-entryPoint",
                campaignId = entryPoint.campaignId,
                apViewId = apViewVMDelegate.getAPViewId(),
                params = mapOf("entryPointId" to entryPoint.id)
            )
        )

        apViewVMDelegate.runActions(actions)
    }

    override fun getAPComponentViewModel(index: Int) : APComponentViewModel? {
        return componentViewModelList[index]
    }

    override fun isActive() : Boolean {
        val userId = userRepository.getAPUserId() ?: ""
        val prefKey = "${userId}_${entryPoint.campaignId}_${APSharedPreferences.IS_CAMPAIGN_WATCHED}"
        return !preferences.getBoolean(prefKey)
    }

    override fun showBorder(): Boolean {
        return apViewVMDelegate.showBorder()
    }

    private fun isReady() : Boolean {
        return componentReadinessSet.size == entryPoint.layers.size
    }

    private fun onComponentReady(index: Int, isReady: Boolean) {
        val oldIsEntryPointReady = componentReadinessSet.size == entryPoint.layers.size

        if (isReady) {
            componentReadinessSet.add(index)
        } else {
            componentReadinessSet.remove(index)
        }

        val newIsEntryPointReady = componentReadinessSet.size == entryPoint.layers.size

        if (oldIsEntryPointReady != newIsEntryPointReady) {
            lifecycleListener.onReady(newIsEntryPointReady)

            if (newIsEntryPointReady && doResumeOnReady) {
                doResumeOnReady = false
                resume()
            }
        }
    }

    private fun onComponentComplete(index: Int) {
        // TODO: implement
    }

    private fun onComponentError(index: Int) {
        lifecycleListener.onError()
    }
}