package plus.adaptive.sdk.ui.apview.newVm

import android.os.Handler
import android.os.Looper
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.story.Campaign
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.ui.apview.APEntryPointLifecycleListener
import plus.adaptive.sdk.ui.apview.vm.APViewVMDelegateProtocol
import plus.adaptive.sdk.ui.components.core.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.core.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModelProvider
import plus.adaptive.sdk.ui.components.story.StoryComponentViewModel

internal class CampaignViewModel(
    private var campaign: Campaign,
    private val preferences: APSharedPreferences,
    private val userRepository: APUserRepository,
    private val lifecycleListener: APEntryPointLifecycleListener,
    private val apViewVMDelegate: APViewVMDelegateProtocol
) : APComponentViewModelProvider, APComponentContainerViewModel{

    private var progressHandler: Handler? = null
    private var isResumed: Boolean = false
    private var doResumeOnReady: Boolean = false
    private val componentViewModel =
        StoryComponentViewModel(this,
            object: APComponentLifecycleListener {
                override fun onReady(isReady: Boolean) { onComponentReady(0, isReady) }
                override fun onComplete() { onComponentComplete(0) }
                override fun onError() { onComponentError(0) }
                override fun onPreparationProgressUpdate(progress: Float) {}
    })

    private val progressCompleteTask = Runnable {
        pause()
        lifecycleListener.onComplete()
    }

    fun runActions(actions: List<APAction?>) {
        apViewVMDelegate.runActions(actions)
    }

    fun pause() {
        isResumed = false
        doResumeOnReady = false
        componentViewModel.pause()
        progressHandler?.removeCallbacks(progressCompleteTask)
    }

    override fun getAPComponentViewModel(index: Int): APComponentViewModel? {
        return componentViewModel
    }

    private fun onComponentReady(index: Int, isReady: Boolean) {
        lifecycleListener.onReady(isReady)
        doResumeOnReady = false
        resume()
    }

    fun resume() {
        if (!isResumed) {
            isResumed = true
            componentViewModel.resume()

            apViewVMDelegate.getAutoScrollPeriod()?.let { autoScrollPeriod ->
                progressHandler?.postDelayed(progressCompleteTask, autoScrollPeriod)
            }
        }
    }

    override fun isActive(): Boolean {
        val userId = userRepository.getAPUserId() ?: ""
        val prefKey = "${userId}_${campaign.id}_${APSharedPreferences.IS_CAMPAIGN_WATCHED}"
        return !preferences.getBoolean(prefKey)
    }

    override fun showBorder(): Boolean {
        return campaign.body.story?.showBorder ?: true
    }

    fun updateStoryShowBorderAndReset(showBorder: Boolean?){
        campaign.body.story?.showBorder = showBorder
        reset()
    }

    fun reset() {
        componentViewModel.reset()
    }

    fun prepare() {
        lifecycleListener.onReady(false)

        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
        Looper.myLooper()?.let {
            progressHandler = Handler(it)
        }
    }

    private fun onComponentComplete(index: Int) {
        // TODO: implement
    }

    private fun onComponentError(index: Int) {
        lifecycleListener.onError()
    }
}