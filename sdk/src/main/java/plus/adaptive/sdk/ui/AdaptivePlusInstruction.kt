package plus.adaptive.sdk.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import plus.adaptive.sdk.data.listeners.APCustomActionListener
import plus.adaptive.sdk.data.models.APStory
import plus.adaptive.sdk.data.models.actions.*
import plus.adaptive.sdk.data.models.story.APTemplateDataModel
import plus.adaptive.sdk.ui.apview.APViewFragment
import plus.adaptive.sdk.ui.apview.vm.APInstructionViewModel
import plus.adaptive.sdk.ui.apview.vm.APInstructionViewModelFactory
import plus.adaptive.sdk.ui.apview.vm.APViewVMDelegateProtocol
import plus.adaptive.sdk.ui.dialogs.WebViewDialog
import plus.adaptive.sdk.ui.stories.APStoriesDialog
import plus.adaptive.sdk.utils.createAPStoryFromStory
import plus.adaptive.sdk.utils.runDelayedTask
import plus.adaptive.sdk.utils.runOnMainThread

class AdaptivePlusInstruction(
    private val fragmentActivity: FragmentActivity,
    private val fragmentManager: FragmentManager? = null
)  : APViewVMDelegateProtocol {

    companion object {
        @JvmStatic
        fun newInstance(
            fragmentActivity: FragmentActivity,
            fragmentManager: FragmentManager? = null
        ) = AdaptivePlusInstruction(fragmentActivity, fragmentManager)
    }

    private lateinit var viewModel: APInstructionViewModel
    private var apCustomActionListener: APCustomActionListener? = null
    private var onStoriesFinishedCallback: (() -> Unit)? = null
    private var viewId = ""
    private val _apStoriesPauseNumberLiveData = MutableLiveData<Int>().apply { value = 0 }
    private val _isAPStoriesPausedLiveData =
        Transformations.map(_apStoriesPauseNumberLiveData) { it > 0 }

    init {
        fragmentActivity?.let {
            val viewModelFactory = APInstructionViewModelFactory(it)
            viewModel = ViewModelProvider(fragmentActivity, viewModelFactory).get(APInstructionViewModel::class.java)
        }
    }

    fun preloadTagById(apViewId: String) {
        viewId = apViewId
        viewModel.requestTemplate(apViewId)
    }

    private fun startStory(component: APTemplateDataModel?) {
        component?.let {
            val stories = ArrayList<APStory>()
            it.campaigns.forEach { campaign ->
                if (campaign.body.instruction != null) {
                    stories.add(createAPStoryFromStory(campaign.body.instruction))
                }
            }
            val apStoriesDialog = APStoriesDialog
                .newInstance(stories, 0, this)
            val count = viewModel.getWatchedInstructionCount(it.campaigns[0].id)
            it.campaigns[0].showCount?.run {
                if(count<this){
                    viewModel.saveInstructionShowCount(it.campaigns[0].id)
                    apStoriesDialog.show(fragmentManagerInstance(), apStoriesDialog.tag)
                } else {
                    onStoriesFinishedCallback?.invoke()
                }
            } ?: {
                apStoriesDialog.show(fragmentManagerInstance(), apStoriesDialog.tag)
            }
        }
    }

    private fun fragmentManagerInstance(): FragmentManager {
        return fragmentManager ?: fragmentActivity.supportFragmentManager
    }

    fun showInstruction() {
        runDelayedTask({
            viewModel.loadTemplateFromCache(apViewId = viewId)?.let {
                startStory(it)
            } ?: onStoriesFinishedCallback?.invoke()
        },500)
    }

    fun setAPCustomActionListener(listener: APCustomActionListener) {
        this.apCustomActionListener = listener
    }

    fun setOnStoriesFinishedCallback(callback: (() -> Unit)?) {
        this.onStoriesFinishedCallback = callback
    }

    override fun runActions(actions: List<APAction?>) {
        for (action in actions) {
            action?.let {
                runAction(it)
            }
        }
    }

    private fun runAction(action: APAction) {
        when (action) {
            is APOpenWebLinkAction -> openWebView(action)
            is APCustomAction -> runAPCustomAction(action)
//            is APShowStoryAction -> showAPStory(action)
            is APSendSMSAction -> sendSms(action)
            is APCallPhoneAction -> callPhone(action)
            else -> {}
        }
    }

    private fun openWebView(action: APOpenWebLinkAction) {
        if (action.isWebView == true) {
            pauseAPStories()

            val webViewDialog = WebViewDialog.newInstance(action.url).apply {
                addOnDismissListener {
                   resumeAPStories()
                }
            }
            fragmentManagerInstance().let {
                webViewDialog.show(it, webViewDialog.tag)
            }
        } else {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
                fragmentActivity.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun runAPCustomAction(action: APCustomAction) {
        action.parameters?.let {
            apCustomActionListener?.onRun(it)
        }
    }

    override fun isAPStoriesPausedLiveData(): LiveData<Boolean> {
        return _isAPStoriesPausedLiveData
    }

    override fun pauseAPStories() {
        _apStoriesPauseNumberLiveData.value = _apStoriesPauseNumberLiveData.value?.inc() ?: 1
    }

    override fun resumeAPStories() {
        _apStoriesPauseNumberLiveData.value = _apStoriesPauseNumberLiveData.value?.dec() ?: 0
    }

    override fun onAPStoriesFinished(campaignId: String?) {
        onStoriesFinishedCallback?.invoke()
    }

    override fun getAutoScrollPeriod(): Long? {
        return null
    }

    override fun showBorder(): Boolean {
        return false
    }

    override fun getAPViewId(): String {
        return viewId
    }

    private fun sendSms(action: APSendSMSAction) {
        val uri = Uri.parse("smsto:${action.phoneNumber}")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", action.message)
        fragmentActivity.startActivity(intent)
    }

    private fun callPhone(action: APCallPhoneAction) {
        val uri = Uri.parse("tel:${action.phoneNumber}")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        fragmentActivity.startActivity(intent)
    }

}